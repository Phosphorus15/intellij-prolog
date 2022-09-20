package tech.phosphorus.intellij.prolog.psi

import com.intellij.extapi.psi.{ASTWrapperPsiElement, StubBasedPsiElementBase}
import com.intellij.lang.ASTNode
import com.intellij.navigation.{ItemPresentation, NavigationItem}
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.stubs.{IStubElementType, StubElement}
import com.intellij.psi.{PsiElement, PsiFileFactory, PsiNameIdentifierOwner, ResolveState}
import com.intellij.util.IncorrectOperationException
import javax.swing.Icon
import tech.phosphorus.intellij.prolog.PrologLanguage
import tech.phosphorus.intellij.prolog.toolchain.PrologPredicateStub

abstract class PrologDeclarationMixin(stub: PrologPredicateStub, stubType: IStubElementType[_ <: StubElement[_], _ <: PsiElement], node: ASTNode) extends StubBasedPsiElementBase[PrologPredicateStub](stub, stubType, node) with PsiNameIdentifierOwner with NavigationItem{

  def this(stub: PrologPredicateStub, stubType: IStubElementType[_ <: StubElement[_], _ <: PsiElement]) {
    this(stub, stubType, null)
  }

  def this(node: ASTNode) {
    this(null, null, node)
  }

  override def getName: String = getNameIdentifier.getText

  override def toString: String = this.getClass.getSimpleName + "(" + this.node.getElementType.toString + ")"

  override def processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement, place: PsiElement): Boolean =
    getNameIdentifier == null || processor.execute(getNameIdentifier, state)

  val elementType: PsiElement = this

  override def getNameIdentifier: PsiElement = if(this.isInstanceOf[PrologPredicateId]) this else findChildByClass(classOf[PrologPredicateId])

  override def setName(s: String): PsiElement = {
    val element = PsiFileFactory.getInstance(getProject).createFileFromText(PrologLanguage.INSTANCE, f"$s.").getFirstChild.getFirstChild.getFirstChild.getFirstChild
    val name = getNameIdentifier
    if(element != null && name != null) name.replace(element)
    else throw new IncorrectOperationException("Rename failed")
  }

  override def getPresentation: ItemPresentation = {
    new ItemPresentation {
      override def getPresentableText: String = getName

      override def getLocationString: String = getContainingFile.getVirtualFile.toString

      override def getIcon(b: Boolean): Icon = null
    }
  }

}

// toplevel to handle declaration finding
abstract class PrologTopLevelDeclarationMixin(node: ASTNode) extends ASTWrapperPsiElement(node) with PsiNameIdentifierOwner {

  override def getName: String = getNameIdentifier.getText

  override def processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement, place: PsiElement): Boolean =
    getNameIdentifier == null || processor.execute(getNameIdentifier, state)

  val elementType: PsiElement = getNameIdentifier

  override def getNameIdentifier: PsiElement = {
    getFirstChild.getFirstChild.getFirstChild
  }

  override def setName(s: String): PsiElement = {
    val element = PsiFileFactory.getInstance(getProject).createFileFromText(PrologLanguage.INSTANCE, f"$s.").getFirstChild.getFirstChild.getFirstChild.getFirstChild
    val name = getNameIdentifier
    if(element != null && name != null) name.replace(element)
    else throw new IncorrectOperationException("Rename failed")
  }

}


