package tech.phosphorus.intellij.prolog.annotator

import com.intellij.lang.annotation.{AnnotationHolder, Annotator, HighlightSeverity}
import com.intellij.psi.PsiElement
import tech.phosphorus.intellij.prolog.inspector.SingletonAnalysis
import tech.phosphorus.intellij.prolog.psi.{PrologToplevelExpr, PrologTrailingExpr}

class SingletonVariableAnnotator extends Annotator {
  override def annotate(psiElement: PsiElement, annotationHolder: AnnotationHolder): Unit = {
    if (psiElement.isInstanceOf[PrologToplevelExpr] || psiElement.isInstanceOf[PrologTrailingExpr]) {
      SingletonAnalysis.resolveLocalAtoms(psiElement).groupBy(_.getText)
        .filter { case (_, elements) => elements.length == 1 }
        .filterNot { case (id, _) => id.startsWith("__") ||
          (id.startsWith("_") && id.charAt(1).isUpper)
        } // this check complies to the newest standard set by swi-prolog
        .foreach { case (id, elements) =>
          annotationHolder.newAnnotation(HighlightSeverity.WEAK_WARNING
            , f"Singleton variable `$id`").range(elements.head)
            .create()
          //(elements.head, f"Singleton variable `$id`")
        }
    }
  }
}
