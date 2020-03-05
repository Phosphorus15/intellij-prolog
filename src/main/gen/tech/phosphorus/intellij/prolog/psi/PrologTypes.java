// This is a generated file. Not intended for manual editing.
package tech.phosphorus.intellij.prolog.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import tech.phosphorus.intellij.prolog.psi.impl.*;

public interface PrologTypes {

  IElementType EXPR_BODY = new PrologElementType("EXPR_BODY");
  IElementType EXPR_HEAD = new PrologElementType("EXPR_HEAD");
  IElementType IDENT = new PrologElementType("IDENT");
  IElementType LOGICAL_AND = new PrologElementType("LOGICAL_AND");
  IElementType LOGICAL_NOT = new PrologElementType("LOGICAL_NOT");
  IElementType LOGICAL_OR = new PrologElementType("LOGICAL_OR");
  IElementType PARAMETER_LIST = new PrologElementType("PARAMETER_LIST");
  IElementType PREDICATE = new PrologElementType("PREDICATE");

  IElementType ATOM_ID = new PrologTokenType("atom_id");
  IElementType COMMA = new PrologTokenType(",");
  IElementType COMMENT = new PrologTokenType("comment");
  IElementType CONST_ID = new PrologTokenType("const_id");
  IElementType DOT = new PrologTokenType(".");
  IElementType EQ = new PrologTokenType("=");
  IElementType LP = new PrologTokenType("(");
  IElementType NOT = new PrologTokenType("/-");
  IElementType NUMBER = new PrologTokenType("number");
  IElementType OPERATOR_ID = new PrologTokenType("operator_id");
  IElementType RP = new PrologTokenType(")");
  IElementType SEMI = new PrologTokenType(";");
  IElementType STRING = new PrologTokenType("string");
  IElementType UNIFY = new PrologTokenType(":-");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == EXPR_BODY) {
        return new PrologExprBodyImpl(node);
      }
      else if (type == EXPR_HEAD) {
        return new PrologExprHeadImpl(node);
      }
      else if (type == IDENT) {
        return new PrologIdentImpl(node);
      }
      else if (type == LOGICAL_AND) {
        return new PrologLogicalAndImpl(node);
      }
      else if (type == LOGICAL_NOT) {
        return new PrologLogicalNotImpl(node);
      }
      else if (type == LOGICAL_OR) {
        return new PrologLogicalOrImpl(node);
      }
      else if (type == PARAMETER_LIST) {
        return new PrologParameterListImpl(node);
      }
      else if (type == PREDICATE) {
        return new PrologPredicateImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
