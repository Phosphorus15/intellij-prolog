package tech.phosphorus.intellij.prolog.annotator

import java.nio.file.{Files, Paths}

import com.intellij.lang.annotation.{Annotation, AnnotationHolder, ExternalAnnotator}
import com.intellij.notification.{NotificationGroup, NotificationType, SingletonNotificationManager}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.{PsiDocumentManager, PsiElement, PsiFile}
import tech.phosphorus.intellij.prolog.inspector.{Error, LinterReport, SwiPrologLinter}
import tech.phosphorus.intellij.prolog.psi._
import tech.phosphorus.intellij.prolog.settings.PrologShowSettingsAction
import tech.phosphorus.intellij.prolog.toolchain.PrologToolchain

import scala.collection.mutable

class AnnotatorTask

case class Task(file: String, workdir: String, linter: SwiPrologLinter) extends AnnotatorTask

case class Abort() extends AnnotatorTask

object GlobalSwiAlertLock {
  var alertAlready = false
}

class PrologExternalAnnotator extends ExternalAnnotator[AnnotatorTask, Array[LinterReport]] {

  lazy val toolchain = new PrologToolchain(Paths.get(PrologToolchain.instanceToolchain()))

  override def collectInformation(file: PsiFile): AnnotatorTask = {
    if (!toolchain.validate()) {
      if (!GlobalSwiAlertLock.alertAlready)
        new SingletonNotificationManager(NotificationGroup.balloonGroup("Prolog Toolchain Not Found"), NotificationType.WARNING, null)
          .notify("Prolog toolchain not detected", "configure a valid toolchain to enable external code linter", file.getProject, null, new PrologShowSettingsAction)
      GlobalSwiAlertLock.alertAlready = true
      Abort()
    } else Task(file.getText, file.getVirtualFile.getParent.getPath, new SwiPrologLinter(toolchain))
  }

  override def doAnnotate(task: AnnotatorTask): Array[LinterReport] = {
    task match {
      case collectedInfo: Task =>
        val application = ApplicationManager.getApplication
        if (application != null && application.isReadAccessAllowed && !application.isUnitTestMode) return Array()
        val tempFile = Files.createTempFile(null, null)
        Files.write(tempFile, collectedInfo.file.getBytes)
        collectedInfo.linter.lintFile(tempFile.toString, collectedInfo.workdir)
          .filterNot(_.message.contains("Singleton variables")) // this one is slipped to internal annotator
      case _ => Array()
    }
  }

  def searchElementAt(file: PsiFile, line: Int): mutable.Seq[PsiElement] = {
    val document = PsiDocumentManager.getInstance(file.getProject).getDocument(file)
    val offset = document.getLineStartOffset(line)
    var element = file.getViewProvider.findElementAt(offset)
    while (element != null && document.getLineNumber(element.getTextOffset) < line) element = element.getNextSibling
    var candidates: mutable.MutableList[PsiElement] = mutable.MutableList()
    while (element != null && document.getLineNumber(element.getTextOffset) == line) {
      candidates += element
      element = element.getNextSibling
    }
    candidates
  }

  def applyAnnotation(file: PsiFile, linterReport: LinterReport, holder: AnnotationHolder): Annotation = {
    // always need this for candidate selection
    //    println(s"${linterReport.location} ${linterReport.line} ${linterReport.message}")
    val document = PsiDocumentManager.getInstance(file.getProject).getDocument(file)
    val candidates = searchElementAt(file, linterReport.line - 1)
    val element = if (linterReport.location.isEmpty) {
      var region = candidates.sortWith(_.getTextRange.getLength > _.getTextRange.getLength).head
      while (region.getParent != null && !(region.isInstanceOf[PrologToplevelExpr] || region.isInstanceOf[PrologTrailingExpr] || region.isInstanceOf[PrologPredicate] || region.isInstanceOf[PrologExprHead] || region.isInstanceOf[PrologExprBody])) {
        region = region.getParent
      }
      region
    } else {
      val targetOffset = calcOffset(document.getText, document.getLineStartOffset(linterReport.line - 1), linterReport.location.get)
      if (targetOffset > 0 && file.getViewProvider.findElementAt(targetOffset) != null) {
        // somewhat wild ?
        file.getViewProvider.findElementAt(targetOffset - 1)
      } else {
        candidates.sortWith(_.getTextRange.getLength > _.getTextRange.getLength).head
      }
    }
    linterReport.ty match {
      case Error() => holder.createErrorAnnotation(element, linterReport.message)
      case _ => holder.createWarningAnnotation(element, linterReport.message)
    }
  }

  def calcOffset(sequence: CharSequence, startOffset: Int, column: Int): Int = {
    var i = 1
    var offset = 0
    while (i < column) {
      val c = Character.codePointAt(sequence, startOffset)
      i = i + (if (c == '\t') 8 else 1)
      offset += 1
    }
    startOffset + offset
  }

  override def apply(file: PsiFile, annotationResult: Array[LinterReport], holder: AnnotationHolder): Unit = {
    super.apply(file, annotationResult, holder)
    annotationResult.foreach(applyAnnotation(file, _, holder))
  }


}
