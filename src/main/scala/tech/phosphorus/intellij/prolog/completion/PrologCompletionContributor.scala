package tech.phosphorus.intellij.prolog.completion

import com.intellij.codeInsight.completion.{CompletionContributor, CompletionParameters, CompletionProvider, CompletionResultSet, CompletionType}
import com.intellij.codeInsight.lookup.{LookupElement, LookupElementBuilder}
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import tech.phosphorus.intellij.prolog.psi.impl.{PrologExprBodyImpl, PrologParameterListImpl}

class PrologSelfCompletionProvider(val list: List[LookupElement]) extends CompletionProvider[CompletionParameters](){
  override def addCompletions(v: CompletionParameters, processingContext: ProcessingContext, completionResultSet: CompletionResultSet): Unit = {
    println("try completing")
    list.foreach(completionResultSet.addElement)
  }
}

class PrologCompletionContributor extends CompletionContributor {
  {
    println("initialize completion")
    extend(CompletionType.BASIC, psiElement, new PrologSelfCompletionProvider(List(
      LookupElementBuilder.create(":-"),
      LookupElementBuilder.create("hello")
    )))
    extend(CompletionType.BASIC
      , psiElement
      , new PrologPredicateStubCompletionProvider())
  }
}
