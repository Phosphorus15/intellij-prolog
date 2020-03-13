package tech.phosphorus.intellij.prolog.psi

import java.io.File

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.resolve.ResolveCache.PolyVariantResolver
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi._
import com.intellij.util.IncorrectOperationException
import tech.phosphorus.intellij.prolog.PrologLanguage

import scala.collection.mutable

abstract class PrologReferenceMixin(node: ASTNode) extends ASTWrapperPsiElement(node) with PsiPolyVariantReference {
  override def isSoft = true

  override def getRangeInElement = new TextRange(0, getTextLength)

  override def getName: String = getText

  override def getElement: PrologReferenceMixin = this

  override def getReference: PrologReferenceMixin = this

  override def getReferences: Array[PsiReference] = Array(this)

  // considering that multiple target might match
  override def isReferenceTo(reference: PsiElement): Boolean = multiResolve(false).contains(reference)

  override def getCanonicalText: String = getText

  override def resolve(): PsiElement = multiResolve(false).headOption.map(_.getElement).orNull

  override def bindToElement(element: PsiElement): PsiElement = throw new IncorrectOperationException("Unsupported")

  override def multiResolve(b: Boolean): Array[ResolveResult] = {
//    println("multi resolve attempt for " + getCanonicalText)
    val file = getContainingFile
    if (file == null) return ResolveResult.EMPTY_ARRAY
    if (!isValid || getProject.isDisposed) return ResolveResult.EMPTY_ARRAY
//    println("entering resolution for file " + file.getName)
    ResolveCache.getInstance(getProject).resolveWithCaching(
      this, new PolyVariantResolver[PrologReferenceMixin] {
        override def resolve(t: PrologReferenceMixin, b: Boolean): Array[ResolveResult] =
          ReferenceResolution.resolveWith(new NameIdentifierResolveProcessor(t.getCanonicalText), t)
      }, true, b, file
    )
//    println("cached result " + cc.length)
  }

  override def handleElementRename(s: String): PsiElement = {
    val element = PsiFileFactory.getInstance(getProject).createFileFromText(PrologLanguage.INSTANCE, f"$s.").getFirstChild.getFirstChild.getFirstChild.getFirstChild
    if (element != null) replace(element)
    else throw new IncorrectOperationException("Rename failed")
  }
}

abstract class ResolveProcessor[T] extends PsiScopeProcessor {
  def getCandidates: Array[ResolveResult]

  override def handleEvent(event: PsiScopeProcessor.Event, associated: Any): Unit = {}

}

class NameIdentifierResolveProcessor(name: String) extends ResolveProcessor[PsiElementResolveResult] {
  val candidates: mutable.HashSet[PsiElementResolveResult] = mutable.HashSet[PsiElementResolveResult]()

  override def getCandidates: Array[ResolveResult] = candidates.toArray

  override def execute(psiElement: PsiElement, resolveState: ResolveState): Boolean = {
    // we should allow multiple-candidates
//    println(f"new element within $psiElement ${psiElement.getClass} \n ${psiElement.getParent.getText}")
    if (psiElement.isInstanceOf[PrologPredicateId]) {
//      println("accepted hash " + psiElement.hashCode() + " " + psiElement.getText + " " + name)
      if (name.equals(psiElement.getText)) {
//        println("finally accepted")
        candidates += new PsiElementResolveResult(psiElement, true)
//        println("candidates len " + candidates.size)
      }
    }
    true // keep processing
  }
}

object ReferenceResolution {
  def treeWalkUp(processor: PsiScopeProcessor,
                 entrance: PsiElement,
                 maxScope: PsiElement,
                 state: ResolveState = ResolveState.initial()): Boolean = {
    if (!entrance.isValid) return false
    var prevParent = entrance
    var scope = entrance

//    println(f"resolution scope $scope")

    while (scope != null) {
//      println(f"resolution scope ${scope.getText} ${scope.getClass}")
      if (!scope.processDeclarations(processor, state, prevParent, entrance)) return false
      if (scope == maxScope) scope = null
      else {
        prevParent = scope
        scope = prevParent.getContext
      }
    }
    true
  }

  def resolveWith[T](processor: ResolveProcessor[T], ref: PsiReference): Array[ResolveResult] = {
    val file = ref.getElement.getContainingFile
//    println("started resolution in file " + file.getName)
    treeWalkUp(processor, ref.getElement, file)
    val c = processor.getCandidates
    println(s"${c.length}")
    c
  }
}
