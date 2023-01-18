package tech.phosphorus.intellij.prolog

import com.intellij.lang.{BracePair, Commenter, PairedBraceMatcher}
import com.intellij.lexer.{FlexAdapter, Lexer}
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.{SyntaxHighlighter, SyntaxHighlighterBase, SyntaxHighlighterFactory}
import com.intellij.openapi.options.colors.{AttributesDescriptor, ColorDescriptor, ColorSettingsPage}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import tech.phosphorus.intellij.prolog.PrologSyntaxHighlighter.{COMMENT, KEYWORD, NUMBER, OPERATION_SIGN, PARENTHESIS, STR}
import tech.phosphorus.intellij.prolog.psi.{PrologFileType, PrologTypes}

import java.util
import javax.swing.Icon

class PrologLexerAdapter extends FlexAdapter(new PrologLexer)

object PrologSyntaxHighlighter {
  val COMMENT: TextAttributesKey = createTextAttributesKey("COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
  val KEYWORD = createTextAttributesKey("KEYWORDS", DefaultLanguageHighlighterColors.KEYWORD)
  val PARENTHESIS = createTextAttributesKey("PARENTS", DefaultLanguageHighlighterColors.PARENTHESES)
  val OPERATION_SIGN = createTextAttributesKey("COMMA_AND", DefaultLanguageHighlighterColors.OPERATION_SIGN)
  val STR = createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING)
  val NUMBER = createTextAttributesKey("NUMERIC_LITERAL", DefaultLanguageHighlighterColors.NUMBER)
}

class PrologSyntaxHighlighter extends SyntaxHighlighterBase {
  override def getHighlightingLexer: Lexer = new PrologLexerAdapter


  override def getTokenHighlights(iElementType: IElementType): Array[TextAttributesKey] = {
    iElementType match {
      case PrologTypes.COMMENT | PrologTypes.BLOCK_COMMENT =>
        Array(COMMENT)
      case PrologTypes.DOT =>
        Array(DefaultLanguageHighlighterColors.SEMICOLON)
      case PrologTypes.UNIFY | PrologTypes.ARITH_EVAL | PrologTypes.WILDCARD =>
        Array(KEYWORD)
      case PrologTypes.LP | PrologTypes.RP | PrologTypes.LB | PrologTypes.RB =>
        Array(PARENTHESIS)
      case PrologTypes.CONST_ID =>
        Array(DefaultLanguageHighlighterColors.INSTANCE_METHOD)
      case PrologTypes.ATOM_ID =>
        Array(DefaultLanguageHighlighterColors.IDENTIFIER)
      case PrologTypes.LOGICAL_AND | PrologTypes.LOGICAL_OR | PrologTypes.OPERATOR_ID =>
        Array(OPERATION_SIGN)
      case PrologTypes.STRING =>
        Array(STR)
      case PrologTypes.INTEGER | PrologTypes.FLOAT =>
        Array(NUMBER)
      case _ => Array()
    }
  }
}

class PrologSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  override def getSyntaxHighlighter(project: Project, virtualFile: VirtualFile)
  : SyntaxHighlighter = new PrologSyntaxHighlighter
}

class PrologBraceMatcher extends PairedBraceMatcher {
  override def getPairs: Array[BracePair] =
    Array(new BracePair(PrologTypes.LP, PrologTypes.RP, false),
      new BracePair(PrologTypes.LB, PrologTypes.RB, true))

  override def isPairedBracesAllowedBeforeType(iElementType: IElementType, iElementType1: IElementType): Boolean = true

  override def getCodeConstructStart(psiFile: PsiFile, i: Int): Int = i
}

class MultiLineCommenter extends Commenter {
  override def getLineCommentPrefix: String = "%"

  override def getBlockCommentPrefix: String = "/*"

  override def getBlockCommentSuffix: String = "*/"

  override def getCommentedBlockCommentPrefix: String = null

  override def getCommentedBlockCommentSuffix: String = null
}

class PrologColorSettingsPage extends ColorSettingsPage {
  private val DESCRIPTORS: Array[AttributesDescriptor] = Array(
    new AttributesDescriptor("Line Comment", PrologSyntaxHighlighter.COMMENT),
    new AttributesDescriptor("Keyword", PrologSyntaxHighlighter.KEYWORD),
    new AttributesDescriptor("Parenthesis", PrologSyntaxHighlighter.PARENTHESIS),
    new AttributesDescriptor("Operation sign", PrologSyntaxHighlighter.OPERATION_SIGN),
    new AttributesDescriptor("String", PrologSyntaxHighlighter.STR),
    new AttributesDescriptor("Number", PrologSyntaxHighlighter.NUMBER)
  )

  override def getAttributeDescriptors(): Array[AttributesDescriptor] = {
    DESCRIPTORS
  }

  override def getIcon(): Icon = {
    PrologFileType.FILE;
  }

  override def getHighlighter(): SyntaxHighlighter = {
    new PrologSyntaxHighlighter();
  }

  override def getDemoText(): String = {
    "greeting(\"Hello World!\").\n"+
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
      "fact(0, R, R) :- !.";
  }

  override def getDisplayName(): String = {
    "Prolog";
  }

  override def getAdditionalHighlightingTagToDescriptorMap: util.Map[String, TextAttributesKey] = null

  override def getColorDescriptors: Array[ColorDescriptor] = new Array[ColorDescriptor](0)
}
