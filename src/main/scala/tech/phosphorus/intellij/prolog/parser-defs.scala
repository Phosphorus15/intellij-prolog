package tech.phosphorus.intellij.prolog

import com.intellij.lang.{ASTNode, ParserDefinition, PsiParser}
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.tree.{IFileElementType, IStubFileElementType, TokenSet}
import com.intellij.psi.{FileViewProvider, PsiElement, PsiFile, TokenType}
import tech.phosphorus.intellij.prolog.psi.{PrologParser, PrologPsiFile, PrologTypes}

object PrologParserDefinition {
  val WHITE_SPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
  val COMMENTS: TokenSet = TokenSet.create(PrologTypes.COMMENT, PrologTypes.BLOCK_COMMENT)

  val FILE = new IStubFileElementType(PrologLanguage.INSTANCE)
}

class PrologParserDefinition extends ParserDefinition {

  override def createLexer(project: Project): Lexer = new PrologLexerAdapter

  override def createParser(project: Project): PsiParser = new PrologParser

  override def getFileNodeType: IFileElementType = PrologParserDefinition.FILE

  override def getCommentTokens: TokenSet = PrologParserDefinition.COMMENTS

  override def getStringLiteralElements: TokenSet = TokenSet.EMPTY

  override def createElement(astNode: ASTNode): PsiElement = PrologTypes.Factory.createElement(astNode)

  override def createFile(fileViewProvider: FileViewProvider): PsiFile = new PrologPsiFile(fileViewProvider)
}