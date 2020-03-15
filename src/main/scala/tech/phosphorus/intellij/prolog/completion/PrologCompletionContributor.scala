package tech.phosphorus.intellij.prolog.completion

import com.intellij.codeInsight.completion.{CompletionContributor, CompletionParameters, CompletionProvider, CompletionResultSet, CompletionType}
import com.intellij.codeInsight.lookup.{LookupElement, LookupElementBuilder}
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import tech.phosphorus.intellij.prolog.psi.PrologTypes
import tech.phosphorus.intellij.prolog.psi.impl.{PrologExprBodyImpl, PrologParameterListImpl}

class PrologSelfCompletionProvider(val list: List[LookupElement]) extends CompletionProvider[CompletionParameters](){
  override def addCompletions(v: CompletionParameters, processingContext: ProcessingContext, completionResultSet: CompletionResultSet): Unit = {
    list.foreach(completionResultSet.addElement)
  }
}

class PrologCompletionContributor extends CompletionContributor {
  {
    extend(CompletionType.BASIC, psiElement, new PrologSelfCompletionProvider(List(
      LookupElementBuilder.create(":-"),
      LookupElementBuilder.create("is")
    )))
    extend(CompletionType.BASIC
      , psiElement
      , new PrologPredicateStubCompletionProvider())
    extend(CompletionType.BASIC
      , psiElement(PrologTypes.ATOM_ID)
      , new PrologAtomCompletion())
  }
}
