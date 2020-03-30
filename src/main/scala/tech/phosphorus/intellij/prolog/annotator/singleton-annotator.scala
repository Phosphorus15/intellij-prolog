package tech.phosphorus.intellij.prolog.annotator

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import tech.phosphorus.intellij.prolog.inspector.SingletonAnalysis
import tech.phosphorus.intellij.prolog.psi.{PrologToplevelExpr, PrologTrailingExpr}

class SingletonVariableAnnotator extends Annotator {
  override def annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder): Unit = {
    if (psiElement.isInstanceOf[PrologToplevelExpr] || psiElement.isInstanceOf[PrologTrailingExpr]) {
      SingletonAnalysis.resolveLocalAtoms(psiElement).groupBy(_.getText).filter(_._2.length == 1)
        .foreach(pair => annotationHolder.createWarningAnnotation(pair._2.head, f"Singleton variable `${pair._1}`"))
    }
  }
}
