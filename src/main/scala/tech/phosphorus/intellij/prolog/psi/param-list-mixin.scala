package tech.phosphorus.intellij.prolog.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

class PrologParameterListMixin(node: ASTNode) extends ASTWrapperPsiElement(node) {

  def recursiveFindShallow(element: PsiElement): Int = {
    element match {
      case logicalAnd: PrologLogicalAnd => 1 + recursiveFindShallow(logicalAnd.getLastChild)
      case equivBinary: PrologEquivBinary => recursiveFindShallow(equivBinary.getFirstChild)
      case null => 0
      case _ => element.getChildren.filter(_.isInstanceOf[PrologEquivBinary]).map(_.getFirstChild).map(recursiveFindShallow).sum
    }
  }

  /** Actually we're just counting how much comma we have in this expression */
  def calculateParameters(): Int = {
    findChildrenByClass(classOf[PrologEquivBinary]).map(recursiveFindShallow(_)).sum + 1
  }

}
