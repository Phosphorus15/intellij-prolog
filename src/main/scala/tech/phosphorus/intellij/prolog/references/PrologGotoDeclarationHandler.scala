package tech.phosphorus.intellij.prolog.references

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import tech.phosphorus.intellij.prolog.PrologLanguage
import tech.phosphorus.intellij.prolog.psi.{PrologParameterListMixin, PrologPredicateId}
import tech.phosphorus.intellij.prolog.psi.impl.PrologRefPredicateIdImpl
import tech.phosphorus.intellij.prolog.toolchain.PrologPredicateStubIndex

class PrologGotoDeclarationHandler extends GotoDeclarationHandler{
  override def getGotoDeclarationTargets(psiElement: PsiElement, i: Int, editor: Editor): Array[PsiElement] = {
    StubIndex.getInstance().getAllKeys(PrologPredicateStubIndex.KEY, psiElement.getProject).toArray.foreach(println(_))
    if (psiElement.getLanguage == PrologLanguage.INSTANCE) {
      val id = psiElement.getParent
      return id match {
        // one step above predicate id gives its full declaration
        case pid: PrologRefPredicateIdImpl =>
          val externals = StubIndex.getElements(PrologPredicateStubIndex.KEY, pid.getText, psiElement.getProject, GlobalSearchScope.allScope(psiElement.getProject), classOf[PrologPredicateId])
          val parameters = pid.getParent.getLastChild
          if(parameters != null && parameters.isInstanceOf[PrologParameterListMixin]) {
            val params = parameters.asInstanceOf[PrologParameterListMixin].calculateParameters()
            println("resolved params " + params)
          }
          (pid.multiResolve(false).map(_.getElement.getParent) ++ externals.toArray[PsiElement](Array()).map(_.getParent)).distinct
        case _ => Array()
      }
    }
    Array()
  }
}
