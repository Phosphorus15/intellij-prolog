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
import tech.phosphorus.intellij.prolog.PrologSyntaxHighlighter._
import tech.phosphorus.intellij.prolog.psi.{PrologFileType, PrologTypes}

import java.util
import javax.swing.Icon

class PrologLexerAdapter extends FlexAdapter(new PrologLexer)

object PrologSyntaxHighlighter {
  val COMMENT: TextAttributesKey = createTextAttributesKey("COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
  val KEYWORD: TextAttributesKey = createTextAttributesKey("KEYWORDS", DefaultLanguageHighlighterColors.KEYWORD)
  val PARENTHESIS: TextAttributesKey = createTextAttributesKey("PARENTS", DefaultLanguageHighlighterColors.PARENTHESES)
  val OPERATION_SIGN: TextAttributesKey = createTextAttributesKey("COMMA_AND", DefaultLanguageHighlighterColors.OPERATION_SIGN)
  val STR: TextAttributesKey = createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING)
  val NUMBER: TextAttributesKey = createTextAttributesKey("NUMERIC_LITERAL", DefaultLanguageHighlighterColors.NUMBER)
  val COMMA: TextAttributesKey = createTextAttributesKey("COMMA", DefaultLanguageHighlighterColors.COMMA)
  val PARAMETER: TextAttributesKey = createTextAttributesKey("PARAMETER", DefaultLanguageHighlighterColors.PARAMETER)
  val IDENTIFIER: TextAttributesKey = createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
  val INSTANCE_METHOD: TextAttributesKey = createTextAttributesKey("INSTANCE_METHOD", DefaultLanguageHighlighterColors.INSTANCE_METHOD)
}

class PrologSyntaxHighlighter extends SyntaxHighlighterBase {
  override def getHighlightingLexer: Lexer = new PrologLexerAdapter


  override def getTokenHighlights(iElementType: IElementType): Array[TextAttributesKey] = {
    iElementType match {
      case PrologTypes.COMMENT | PrologTypes.BLOCK_COMMENT =>
        Array(COMMENT)
      case PrologTypes.DOT =>
        Array(DefaultLanguageHighlighterColors.SEMICOLON)
      case PrologTypes.UNIFY | PrologTypes.ARITH_EVAL | PrologTypes.WILDCARD | PrologTypes.EXPAND | PrologTypes.SEMI =>
        Array(KEYWORD)
      case PrologTypes.LP | PrologTypes.RP | PrologTypes.LB | PrologTypes.RB =>
        Array(PARENTHESIS)
      case PrologTypes.CONST_ID =>
        Array(DefaultLanguageHighlighterColors.INSTANCE_METHOD)
      case PrologTypes.LOGICAL_AND | PrologTypes.LOGICAL_OR | PrologTypes.OPERATOR_ID | PrologTypes.RUNTIME_EVALUATION =>
        Array(OPERATION_SIGN)
      case PrologTypes.STRING =>
        Array(STR)
      case PrologTypes.INTEGER | PrologTypes.FLOAT =>
        Array(NUMBER)
      case PrologTypes.COMMA | PrologTypes.LIST_CONS =>
        Array(COMMA)
      case PrologTypes.COMMON_PREDICATE =>
        Array(PARAMETER)
      case PrologTypes.ATOM_ID =>
        Array(IDENTIFIER)
      case PrologTypes.COMMON_VAL =>
        Array(INSTANCE_METHOD)
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
    new AttributesDescriptor("Operator", PrologSyntaxHighlighter.OPERATION_SIGN),
    new AttributesDescriptor("String", PrologSyntaxHighlighter.STR),
    new AttributesDescriptor("Number", PrologSyntaxHighlighter.NUMBER),
    new AttributesDescriptor("Comma", PrologSyntaxHighlighter.COMMA),
    new AttributesDescriptor("Parameter", PrologSyntaxHighlighter.PARAMETER),
    new AttributesDescriptor("Identifier", PrologSyntaxHighlighter.IDENTIFIER),
    new AttributesDescriptor("Predicate Identifier", PrologSyntaxHighlighter.INSTANCE_METHOD)
  )

  override def getAttributeDescriptors: Array[AttributesDescriptor] = {
    DESCRIPTORS
  }

  override def getIcon: Icon = {
    PrologFileType.FILE;
  }

  override def getHighlighter: SyntaxHighlighter = {
    new PrologSyntaxHighlighter();
  }

  override def getDemoText: String = {
    """greeting("Hello World!").
      |len(0, []).
      |len(Len, [_|Tail]) :- len(Lsub, Tail), Len is Lsub + 1.
      |
      |sum(0, []).
      |sum(Sum, [Head|Tail]) :- sum(LSum, Tail), Sum is LSum + Head.
      |
      |avg(Avg, List) :- len(Len, List), sum(Sum, List), Avg is Sum/Len.
      |
      |append([], Item, Out) :- Out = [Item].
      |append([Single], Item, Out) :- Out = [Single, Item].
      |append([Head|Tail], Item, Out) :- append(Tail, Item, Ret), Out = [Head|Ret].
      |
      |concat([], List, List).
      |concat(List, [], List).
      |%%concat(List, [Head|Tail], Out) :- append(List, Head, Ret), concat(Ret, Tail, Value), Out = Value.
      |concat([Head|Tail], List, [Head|Tail1]) :- concat(Tail, List, Tail1).
      |
      |fib(N, R) :- fib(N, 0, 1, R).
      |fib(0, C1, _, C1) :- !.
      |fib(N, C1, C2, R) :-
      |	C3 is C1 + C2,
      |	N1 is N - 1,
      |	fib(N1, C2, C3, R).
      |
      |fact(N, R) :- fact(N, 1, R).
      |
      |fact(N, C, R) :-
      |	N1 is N - 1,
      |	C1 is C * N,
      |	fact(N1, C1, R).
      |
      |dbretrieve2(TupleDesc, QueueNum, TupCall ) :-
      | TupleDesc =.. [_:TupleList] ,%TupleHead
      | repeat,
      | p_getTuple(TupleList,Eof, TupCall) ,
      | ( (Eof == true, p_freeQueue(QueueNum) ,! ,fail) ;
      | true) .
      |
      |fact(0, R, R) :- !.""".replace("\r", "").stripMargin;
  }

  override def getDisplayName: String = {
    "Prolog";
  }

  override def getAdditionalHighlightingTagToDescriptorMap: util.Map[String, TextAttributesKey] = null

  override def getColorDescriptors: Array[ColorDescriptor] = new Array[ColorDescriptor](0)
}
