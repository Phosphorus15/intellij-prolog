package tech.phosphorus.intellij.prolog.references

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import tech.phosphorus.intellij.prolog.PrologLanguage
import tech.phosphorus.intellij.prolog.psi.impl.PrologRefPredicateIdImpl
import tech.phosphorus.intellij.prolog.psi.{PrologAtomReferenceMixin, PrologPredicateId}
import tech.phosphorus.intellij.prolog.toolchain.PrologPredicateStubIndex
import tech.phosphorus.intellij.prolog.SingletonObject._

class PrologGotoDeclarationHandler extends GotoDeclarationHandler{
  override def getGotoDeclarationTargets(psiElement: PsiElement, i: Int, editor: Editor): Array[PsiElement] = {
    if (psiElement.getLanguage == PrologLanguage.INSTANCE) {
      val id = psiElement.getParent
      return id match {
        // one step above predicate id gives its full declaration
        case pid: PrologRefPredicateIdImpl =>
          val externals = StubIndex.getElements(PrologPredicateStubIndex.KEY, pid.getText, psiElement.getProject, GlobalSearchScope.allScope(psiElement.getProject), classOf[PrologPredicateId])
          (pid.multiResolve(false).map(_.getElement.getParent) ++ externals.toArray[PsiElement](Array[PsiElement]()).map(_.getParent)).distinct
        case pid: PrologAtomReferenceMixin =>
          pid.resolve().singleton
        case _ => Array()
      }
    }
    Array()
  }
}
