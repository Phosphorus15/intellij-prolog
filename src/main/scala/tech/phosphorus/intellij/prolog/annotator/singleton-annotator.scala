package tech.phosphorus.intellij.prolog.annotator

import com.intellij.lang.annotation.{AnnotationHolder, Annotator}
import com.intellij.psi.PsiElement
import tech.phosphorus.intellij.prolog.inspector.SingletonAnalysis
import tech.phosphorus.intellij.prolog.psi.{PrologToplevelExpr, PrologTrailingExpr}

class SingletonVariableAnnotator extends Annotator {
  override def annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder): Unit = {
    if (psiElement.isInstanceOf[PrologToplevelExpr] || psiElement.isInstanceOf[PrologTrailingExpr]) {
      SingletonAnalysis.resolveLocalAtoms(psiElement).groupBy(_.getText)
        .filter { case (_, elements) => elements.length == 1 }
        .foreach { case (id, elements) => annotationHolder.createWarningAnnotation(elements.head, f"Singleton variable `$id`") }
    }
  }
}
