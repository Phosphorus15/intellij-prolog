package tech.phosphorus.intellij.prolog.formatting

import com.intellij.formatting._
import com.intellij.lang.{ASTNode, Language}
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider.SettingsType
import com.intellij.psi.codeStyle.{CodeStyleSettings, CodeStyleSettingsCustomizable, LanguageCodeStyleSettingsProvider}
import com.intellij.psi.formatter.common.AbstractBlock
import tech.phosphorus.intellij.prolog.PrologLanguage
import tech.phosphorus.intellij.prolog.formatting.PrologFormattingModelBuilder.createSpaceBuilder
import tech.phosphorus.intellij.prolog.psi.PrologTypes

import java.util
import scala.annotation.tailrec

class PrologFormattingModelBuilder extends FormattingModelBuilder {
  override def createModel(formattingContext: FormattingContext): FormattingModel = {
    val codeStyleSettings = formattingContext.getCodeStyleSettings
    FormattingModelProvider
      .createFormattingModelForPsiFile(formattingContext.getContainingFile,
        new RuleBlock(formattingContext.getNode,
          Wrap.createWrap(WrapType.NONE, false),
          Alignment.createAlignment(),
          createSpaceBuilder(codeStyleSettings)),
        codeStyleSettings)
  }
}

object PrologFormattingModelBuilder {
  def createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder = {
    return new SpacingBuilder(settings, PrologLanguage.INSTANCE)
      .between(PrologTypes.UNIFY, PrologTypes.DOT)
      .spaceIf(settings.getCommonSettings(PrologLanguage.INSTANCE.getID).ALIGN_MULTILINE_METHOD_BRACKETS);
  }
}

class RuleBlock(node: ASTNode, wrap: Wrap, alignment: Alignment, spacingBuilder: SpacingBuilder) extends AbstractBlock(node, wrap, alignment) {
  override def buildChildren(): util.List[Block] = {
    val blocks = new util.ArrayList[Block]()
    addBlocks(node.getFirstChildNode, blocks)
    return blocks
  }

  @tailrec
  private def addBlocks(child: ASTNode, blocks: util.ArrayList[Block]): Unit = {
    if (child != null) {
      child.getElementType match {
        case PrologTypes.UNIFY => wrapblock(child, blocks)
        case PrologTypes.COMMA => wrapblock(child, blocks)
        case PrologTypes.DOT => wrapblock(child, blocks)
        case TokenType.WHITE_SPACE => wrapblock(child, blocks)
        case non_white_space_rest => blocks.add(new RuleBlock(child, Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(),
          spacingBuilder))
      }
      addBlocks(child.getTreeNext, blocks)
    }
  }

  private def wrapblock(child: ASTNode, blocks: util.ArrayList[Block]) = {
    val block = new RuleBlock(child, Wrap.createWrap(WrapType.ALWAYS, false), Alignment.createAlignment(),
      spacingBuilder)
    blocks.add(block)

  }

  override def getIndent: Indent = Indent.getNoneIndent

  override def getSpacing(child1: Block, child2: Block): Spacing = spacingBuilder.getSpacing(this, child1, child2)

  override def isLeaf: Boolean = myNode.getFirstChildNode == null
}

class PrologLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {


  override def getLanguage: Language = PrologLanguage.INSTANCE


  override def customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: LanguageCodeStyleSettingsProvider.SettingsType): Unit = {
    if (settingsType == SettingsType.INDENT_SETTINGS) {
      consumer.showStandardOptions("ALIGN_MULTILINE_METHOD_BRACKETS")
      consumer.renameStandardOption("ALIGN_MULTILINE_METHOD_BRACKETS", "indent rules")
    }
  }

  override def getCodeSample(settingsType: LanguageCodeStyleSettingsProvider.SettingsType): String = "greeting(\"Hello World!\").\n" +
    "len(0, []).\n" +
    "len(Len, [_|Tail]) :- len(Lsub, Tail), Len is Lsub + 1.\n" +
    "\n" +
    "sum(0, []).\n" +
    "sum(Sum, [Head|Tail]) :- sum(LSum, Tail), Sum is LSum + Head.\n" +
    "\n" +
    "avg(Avg, List) :- len(Len, List), sum(Sum, List), Avg is Sum/Len.\n" +
    "\n" +
    "append([], Item, Out) :- Out = [Item].\n" +
    "append([Single], Item, Out) :- Out = [Single, Item].\n" +
    "append([Head|Tail], Item, Out) :- append(Tail, Item, Ret), Out = [Head|Ret].\n" +
    "\n" +
    "concat([], List, List).\n" +
    "concat(List, [], List).\n" +
    "%concat(List, [Head|Tail], Out) :- append(List, Head, Ret), concat(Ret, Tail, Value), Out = Value.\n" +
    "concat([Head|Tail], List, [Head|Tail1]) :- concat(Tail, List, Tail1).\n" +
    "\n" +
    "fib(N, R) :- fib(N, 0, 1, R).\n" +
    "fib(0, C1, _, C1) :- !.\n" +
    "fib(N, C1, C2, R) :-\n" +
    "\tC3 is C1 + C2,\n" +
    "\tN1 is N - 1,\n" +
    "\tfib(N1, C2, C3, R).\n" +
    "\n" +
    "fact(N, R) :- fact(N, 1, R).\n" +
    "\n" +
    "fact(N, C, R) :-\n" +
    "\tN1 is N - 1,\n" +
    "\tC1 is C * N,\n" +
    "\tfact(N1, C1, R).\n" +
    "\n" +
    "fact(0, R, R) :- !."
}

//class PrologStyleSettings(tagName: String, container: CodeStyleSettings) extends CustomCodeStyleSettings(tagName, container) {
//
//}

