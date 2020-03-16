package tech.phosphorus.intellij.prolog.completion

import com.intellij.codeInsight.completion.{CompletionParameters, CompletionProvider, CompletionResultSet}
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ProcessingContext
import tech.phosphorus.intellij.prolog.psi.{PrologParameterListMixin, PrologPredicateId}
import tech.phosphorus.intellij.prolog.toolchain.PrologPredicateStubIndex


class PrologPredicateStubCompletionProvider extends CompletionProvider[CompletionParameters](){
  override def addCompletions(v: CompletionParameters, processingContext: ProcessingContext, completionResultSet: CompletionResultSet): Unit = {
    val psi = v.getOriginalPosition
    val project = v.getEditor.getProject
    if(psi == null || project == null) return
    PrologPredicateStubIndex.getAllKeys(project).filter(_.contains(psi.getText)).foreach(key => {
      val elements = StubIndex.getElements(PrologPredicateStubIndex.KEY, key, project, GlobalSearchScope.allScope(project), classOf[PrologPredicateId])
      elements.toArray(Array[PsiElement]()).foreach(element => {
        val parameterList = element.getParent.getLastChild
//        println(parameterList.getClass)
        val parameters = parameterList match {
          case mixin: PrologParameterListMixin =>
            "/" + mixin.calculateParameters()
          case _ => null
        }
        completionResultSet.addElement(
          // TODO handle parameter variants
          LookupElementBuilder.create(element.getText).withLookupString(element.getText + parameters).withPsiElement(element).withPresentableText(element.getText + parameters)
            .withTypeText(element.getContainingFile.getVirtualFile.getPath)
        )
      })
    })
  }
}
