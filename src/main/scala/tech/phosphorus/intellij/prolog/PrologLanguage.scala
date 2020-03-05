package tech.phosphorus.intellij.prolog

import com.intellij.lexer.{FlexAdapter, Lexer}
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.{SyntaxHighlighter, SyntaxHighlighterBase, SyntaxHighlighterFactory}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
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
        Array(createTextAttributesKey("DOT_ENDING", DefaultLanguageHighlighterColors.SEMICOLON))
      case PrologTypes.UNIFY =>
        Array(createTextAttributesKey("KEYWORDS", DefaultLanguageHighlighterColors.KEYWORD))
      case PrologTypes.LP | PrologTypes.RP =>
        Array(createTextAttributesKey("PARENTS", DefaultLanguageHighlighterColors.PARENTHESES))
      case PrologTypes.CONST_ID =>
        Array(createTextAttributesKey("CONSTANTS", DefaultLanguageHighlighterColors.IDENTIFIER))
      case PrologTypes.ATOM_ID =>
        Array(createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.LOCAL_VARIABLE))
      case PrologTypes.LOGICAL_AND | PrologTypes.LOGICAL_OR | PrologTypes.OPERATOR_ID =>
        Array(createTextAttributesKey("COMMA_AND", DefaultLanguageHighlighterColors.OPERATION_SIGN))
      case _ => Array()
    }
  }
}

class PrologSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  override def getSyntaxHighlighter(project: Project, virtualFile: VirtualFile)
        : SyntaxHighlighter = new PrologSyntaxHighlighter
}
