package tech.phosphorus.intellij.prolog.psi

import com.intellij.psi.PsiElement

object PrologPsiUtil {

  def findParentWith(element: PsiElement, pred: PsiElement => Boolean): Option[PsiElement] =
    Stream.iterate(element)(current => current.getParent)
      .takeWhile(_ != null).find(it => pred(it))

}
