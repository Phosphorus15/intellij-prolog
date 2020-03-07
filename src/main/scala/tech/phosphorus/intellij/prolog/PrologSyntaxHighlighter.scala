package tech.phosphorus.intellij.prolog

import com.intellij.lang.{BracePair, Commenter, PairedBraceMatcher}
import com.intellij.lexer.{FlexAdapter, Lexer}
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.{SyntaxHighlighter, SyntaxHighlighterBase, SyntaxHighlighterFactory}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import tech.phosphorus.intellij.prolog.psi.PrologTypes

class PrologLexerAdapter extends FlexAdapter(new PrologLexer)

class PrologSyntaxHighlighter extends SyntaxHighlighterBase {
  override def getHighlightingLexer: Lexer = new PrologLexerAdapter

  override def getTokenHighlights(iElementType: IElementType): Array[TextAttributesKey] = {
    iElementType match {
      case PrologTypes.COMMENT =>
        Array(createTextAttributesKey("LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT))
      case PrologTypes.DOT =>
        Array(DefaultLanguageHighlighterColors.SEMICOLON)
      case PrologTypes.UNIFY | PrologTypes.ARITH_EVAL | PrologTypes.WILDCARD =>
        Array(createTextAttributesKey("KEYWORDS", DefaultLanguageHighlighterColors.KEYWORD))
      case PrologTypes.LP | PrologTypes.RP | PrologTypes.LB | PrologTypes.RB =>
        Array(createTextAttributesKey("PARENTS", DefaultLanguageHighlighterColors.PARENTHESES))
      case PrologTypes.CONST_ID =>
        Array(DefaultLanguageHighlighterColors.INSTANCE_METHOD)
      case PrologTypes.ATOM_ID =>
        Array(DefaultLanguageHighlighterColors.IDENTIFIER)
      case PrologTypes.LOGICAL_AND | PrologTypes.LOGICAL_OR | PrologTypes.OPERATOR_ID =>
        Array(createTextAttributesKey("COMMA_AND", DefaultLanguageHighlighterColors.OPERATION_SIGN))
      case PrologTypes.STRING =>
        Array(createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING))
      case PrologTypes.INTEGER | PrologTypes.FLOAT =>
        Array(createTextAttributesKey("NUMERIC_LITERAL", DefaultLanguageHighlighterColors.NUMBER))
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
