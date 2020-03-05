package tech.phosphorus.intellij.prolog

import com.intellij.psi.tree.IElementType

class PrologTokenType(debugName : String) extends IElementType(debugName, PrologLanguage.INSTANCE)

object PrologTokenType {
  val DEFINITION = new PrologTokenType("DEFINITION")
  val IDENTIFIER_UPCASE = new PrologTokenType("IDENTIFIER_UPCASE")
  val IDENTIFIER_DOWNCASE = new PrologTokenType("IDENTIFIER_DOWNCASE")
  val EQ = new PrologTokenType("EQ")
  val WILDCARD = new PrologTokenType("WILDCARD")
  val SEMI_OR = new PrologTokenType("SEMI_OR")
  val COMMA_AND = new PrologTokenType("COMMA_AND")
  val DOT = new PrologTokenType("DOT")
  val LPAREN = new PrologTokenType("LPAREN")
  val RPAREN = new PrologTokenType("RPAREN")
  val LBRACKET = new PrologTokenType("LBRACKET")
  val RBRACKET = new PrologTokenType("RBRACKET")
  val LINE_COMMENT = new PrologTokenType("LINE_COMMENT")
}
