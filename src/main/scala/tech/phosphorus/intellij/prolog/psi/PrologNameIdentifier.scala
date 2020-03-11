package tech.phosphorus.intellij.prolog.psi


import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.{PsiElement, PsiFileFactory, PsiNameIdentifierOwner, ResolveState}
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.util.IncorrectOperationException
import tech.phosphorus.intellij.prolog.PrologLanguage

abstract class PrologNameIdentifier(node: ASTNode) extends ASTWrapperPsiElement(node) with PsiNameIdentifierOwner {

  override def getName: String = getText

  override def getNameIdentifier: PsiElement = this

  override def processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement, place: PsiElement): Boolean =
    processor.execute(getNameIdentifier, state)

  override def setName(s: String): PsiElement = {
    val element = PsiFileFactory.getInstance(getProject).createFileFromText(PrologLanguage.INSTANCE, f"$s.").getFirstChild.getFirstChild.getFirstChild.getFirstChild
    val name = getNameIdentifier
    if(element != null && name != null) name.replace(element)
    else throw new IncorrectOperationException("Rename failed")
  }
}
