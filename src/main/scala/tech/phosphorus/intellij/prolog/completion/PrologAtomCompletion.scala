package tech.phosphorus.intellij.prolog.completion

import com.intellij.codeInsight.completion.{CompletionParameters, CompletionProvider, CompletionResultSet}
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import tech.phosphorus.intellij.prolog.psi.{PrologIdent, PrologPsiUtil, PrologToplevelExpr, PrologTrailingExpr, PrologTypes}

class PrologAtomCompletion extends CompletionProvider[CompletionParameters]() {
  override def addCompletions(v: CompletionParameters, processingContext: ProcessingContext, completionResultSet: CompletionResultSet): Unit = {
    val psi = v.getOriginalPosition
    val project = v.getEditor.getProject
    if(psi == null || project == null) return
    if(psi.getNode.getElementType == PrologTypes.ATOM_ID) {
      val top = PrologPsiUtil.findParentWith(psi, _.isInstanceOf[PrologToplevelExpr])
        .getOrElse(PrologPsiUtil.findParentWith(psi, _.isInstanceOf[PrologTrailingExpr]).get)
      resolveLocalAtoms(top).filterNot(_.getText == psi.getText).filter(_.getText.contains(psi.getText))
        .foreach( element =>
          completionResultSet.addElement(
            LookupElementBuilder.create(element.getText)
          )
        )
    }
  }

  def resolveLocalAtoms(top: PsiElement): Array[PsiElement] = {
    top match {
      case ident: PrologIdent => if (ident.getAtomId != null) {Array(ident)} else Array()
      case _ => top.getChildren.flatMap(resolveLocalAtoms)
    }
  }
}
