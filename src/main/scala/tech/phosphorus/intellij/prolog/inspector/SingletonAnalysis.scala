package tech.phosphorus.intellij.prolog.inspector

import com.intellij.psi.PsiElement
import tech.phosphorus.intellij.prolog.SingletonObject._
import tech.phosphorus.intellij.prolog.psi.PrologIdent

object SingletonAnalysis {
  def resolveLocalAtoms(top: PsiElement): Array[PsiElement] = {
    top match {
      case ident: PrologIdent => if (ident.getAtomId != null) ident.singleton else Array()
      case _ => top.getChildren.flatMap(resolveLocalAtoms)
    }
  }

  /// Non-caching and slow to use recursive searching
  /// Returns in the ascending order of occurrence position
  /// Cache this else way ?
  def findAllLocalAtom(element: PsiElement, string: String): Array[PsiElement] = {
    resolveLocalAtoms(element).filter(_.getText == string).sortBy(_.getTextRange.getStartOffset)
  }

  def isLocalSingleton(element: PsiElement, const: PsiElement): Boolean = findAllLocalAtom(element, const.getText).length == 1
}
