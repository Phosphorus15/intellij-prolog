package tech.phosphorus.intellij.prolog

import com.intellij.lang.Language
import com.intellij.lexer.{FlexAdapter, Lexer}
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.{SyntaxHighlighter, SyntaxHighlighterBase, SyntaxHighlighterFactory}
import com.intellij.psi.tree.IElementType
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class PrologLexerAdapter extends FlexAdapter(new PrologLexer)

class PrologSyntaxHighlighter extends SyntaxHighlighterBase {
  override def getHighlightingLexer: Lexer = new PrologLexerAdapter

  override def getTokenHighlights(iElementType: IElementType): Array[TextAttributesKey] = {
    iElementType match {
      case PrologTokenType.LINE_COMMENT =>
        Array(createTextAttributesKey("LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT))
      case PrologTokenType.WILDCARD | PrologTokenType.DOT =>
        Array(createTextAttributesKey("LINE_COMMENT", DefaultLanguageHighlighterColors.KEYWORD))
      case _ => Array()
    }
  }
}

class PrologSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  override def getSyntaxHighlighter(project: Project, virtualFile: VirtualFile)
        : SyntaxHighlighter = new PrologSyntaxHighlighter
}
