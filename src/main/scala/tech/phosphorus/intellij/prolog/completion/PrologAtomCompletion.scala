package tech.phosphorus.intellij.prolog.completion

import com.intellij.codeInsight.completion.{CompletionParameters, CompletionProvider, CompletionResultSet}
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.testFramework.exceptionCases.IncorrectOperationExceptionCase
import com.intellij.util.{IncorrectOperationException, ProcessingContext}
import tech.phosphorus.intellij.prolog.inspector.SingletonAnalysis
import tech.phosphorus.intellij.prolog.psi.{PrologIdent, PrologPsiUtil, PrologToplevelExpr, PrologTrailingExpr, PrologTypes}

class PrologAtomCompletion extends CompletionProvider[CompletionParameters]() {
  override def addCompletions(v: CompletionParameters, processingContext: ProcessingContext, completionResultSet: CompletionResultSet): Unit = {
    val psi = v.getOriginalPosition
    val project = v.getEditor.getProject
    if (psi == null || project == null) return
    if (psi.getNode.getElementType == PrologTypes.ATOM_ID) {
      val top = PrologPsiUtil
        .findParentWith(psi, element =>
          element.isInstanceOf[PrologToplevelExpr]
          || element.isInstanceOf[PrologTrailingExpr]).getOrElse(throw new IncorrectOperationException("unable top locate parent psi element"))
      SingletonAnalysis.resolveLocalAtoms(top).filterNot(_.getText == psi.getText).filter(_.getText.contains(psi.getText))
        .foreach(element =>
          completionResultSet.addElement(
            LookupElementBuilder.create(element.getText)
          )
        )
    }
  }
}
