package tech.phosphorus.intellij.prolog.psi

import com.intellij.psi.PsiElement

object PrologPsiUtil {

  def findParentWith(element: PsiElement, pred: (PsiElement) => Boolean): Option[PsiElement] = {
    var current = element
    while (current != null) {
      current = current.getParent
      if (pred(current)) return Some(current)
    }
    None
  }

}
