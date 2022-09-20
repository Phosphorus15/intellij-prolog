package tech.phosphorus.intellij.prolog.annotator

import java.nio.file.{Files, Paths}
import com.intellij.lang.annotation.{Annotation, AnnotationBuilder, AnnotationHolder, ExternalAnnotator, HighlightSeverity}
import com.intellij.notification.{NotificationDisplayType, NotificationGroup, NotificationGroupManager, NotificationType, SingletonNotificationManager}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.{PsiDocumentManager, PsiElement, PsiFile}
import tech.phosphorus.intellij.prolog.inspector.{Error, LinterReport, SwiPrologLinter}
import tech.phosphorus.intellij.prolog.psi._
import tech.phosphorus.intellij.prolog.settings.PrologShowSettingsAction
import tech.phosphorus.intellij.prolog.toolchain.PrologToolchain

import scala.annotation.tailrec
import scala.collection.mutable

class AnnotatorTask

case class Task(file: String, workdir: String, linter: SwiPrologLinter) extends AnnotatorTask

case class Abort() extends AnnotatorTask

object GlobalSwiAlertLock {
  var alertAlready = false
}

class PrologExternalAnnotator extends ExternalAnnotator[AnnotatorTask, Array[LinterReport]] {

  lazy val toolchain = new PrologToolchain(Paths.get(PrologToolchain.instanceToolchain()))

  lazy val notificationManager: NotificationGroupManager = ApplicationManager.getApplication.getService(classOf[NotificationGroupManager])

  override def collectInformation(file: PsiFile): AnnotatorTask = {
    if (!toolchain.validate()) {
      if (!GlobalSwiAlertLock.alertAlready)
        notificationManager
          .getNotificationGroup("Prolog Plugin Notification")
          .createNotification("Prolog toolchain not detected", "Configure a valid toolchain to enable external code linter", NotificationType.WARNING)
          .addAction(new PrologShowSettingsAction)
          .notify(file.getProject)
      //        new SingletonNotificationManager("", NotificationType.WARNING)
      //          .notify("Prolog toolchain not detected", "configure a valid toolchain to enable external code linter", file.getProject, null, new PrologShowSettingsAction)
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
    val element = file.getViewProvider.findElementAt(offset)
    Stream.iterate(element)(_.getNextSibling)
      .takeWhile(it => it != null && document.getLineNumber(it.getTextOffset) < line)
      .lastOption match {
      case Some(element) =>
        Stream.iterate(element)(_.getNextSibling)
          .takeWhile(it => it != null && document.getLineNumber(it.getTextOffset) == line)
          .to(mutable.MutableList.canBuildFrom)
      case _ => mutable.MutableList()
    }
  }

  @tailrec
  final def findTop(region: PsiElement): PsiElement =
    if (region.getParent == null ||
      (region.isInstanceOf[PrologToplevelExpr] && region.isInstanceOf[PrologTrailingExpr]
        && region.isInstanceOf[PrologPredicate] && region.isInstanceOf[PrologExprHead]
        && region.isInstanceOf[PrologExprBody])) region
    else findTop(region.getParent)

  def applyAnnotation(file: PsiFile, linterReport: LinterReport, holder: AnnotationHolder): AnnotationBuilder = {
    // always need this for candidate selection
    //    println(s"${linterReport.location} ${linterReport.line} ${linterReport.message}")
    val document = PsiDocumentManager.getInstance(file.getProject).getDocument(file)
    val candidates = searchElementAt(file, linterReport.line - 1)
    val element = if (linterReport.location.isEmpty) {
      findTop(candidates.sortWith(_.getTextRange.getLength > _.getTextRange.getLength).head)
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
      case Error() => holder.newAnnotation(HighlightSeverity.ERROR, linterReport.message).range(element) // createErrorAnnotation(element, linterReport.message)
      case _ => holder.newAnnotation(HighlightSeverity.WARNING, linterReport.message).range(element) // holder.createWarningAnnotation(element, linterReport.message)
    }
  }

  def calcOffset(sequence: CharSequence, startOffset: Int, column: Int): Int = {
    val offset = Stream.iterate((1, 0)) { case (i, off) =>
      (i + (if (Character.codePointAt(sequence, off) == '\t') 8 else 1), off + 1)
    }.takeWhile { case (i, _) => i < column }
      .lastOption.map { case (_, off) => off }.getOrElse(0)
    startOffset + offset
  }

  override def apply(file: PsiFile, annotationResult: Array[LinterReport], holder: AnnotationHolder): Unit = {
    super.apply(file, annotationResult, holder)
    annotationResult.foreach(applyAnnotation(file, _, holder).notify())
  }


}
