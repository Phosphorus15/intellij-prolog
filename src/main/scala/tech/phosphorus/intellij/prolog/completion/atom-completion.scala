package tech.phosphorus.intellij.prolog.completion

import com.intellij.codeInsight.completion.{CompletionParameters, CompletionProvider, CompletionResultSet}
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.{IncorrectOperationException, ProcessingContext}
import tech.phosphorus.intellij.prolog.inspector.SingletonAnalysis
import tech.phosphorus.intellij.prolog.psi.{PrologPsiUtil, PrologToplevelExpr, PrologTrailingExpr, PrologTypes}
import tech.phosphorus.intellij.prolog.OptionLetIn._

class PrologAtomCompletion extends CompletionProvider[CompletionParameters]() {

  def failSilent(err: String): Unit = {

  }

  override def addCompletions(v: CompletionParameters, processingContext: ProcessingContext, completionResultSet: CompletionResultSet): Unit = {
    val psi = v.getOriginalPosition
    val project = v.getEditor.getProject
    if (psi == null || project == null) return
    if (psi.getNode.getElementType == PrologTypes.ATOM_ID) {
      PrologPsiUtil
        .findParentWith(psi, element =>
          element.isInstanceOf[PrologToplevelExpr]
            || element.isInstanceOf[PrologTrailingExpr]).let { top =>
        SingletonAnalysis.resolveLocalAtoms(top).filterNot(_.getText == psi.getText).filter(_.getText.contains(psi.getText))
          .foreach(element =>
            completionResultSet.addElement(
              LookupElementBuilder.create(element.getText)
            )
          )
      }.getOrElse(failSilent("unable top locate parent psi element"))
    }
  }
}
