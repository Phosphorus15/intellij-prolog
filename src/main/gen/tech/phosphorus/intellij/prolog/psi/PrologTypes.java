// This is a generated file. Not intended for manual editing.
package tech.phosphorus.intellij.prolog.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import tech.phosphorus.intellij.prolog.psi.impl.*;

public interface PrologTypes {

  IElementType ARITHMETIC_EVAL = new PrologElementType("ARITHMETIC_EVAL");
  IElementType COMMON_VAL = new PrologElementType("COMMON_VAL");
  IElementType EQUIV_BINARY = new PrologElementType("EQUIV_BINARY");
  IElementType EXPR_BODY = new PrologElementType("EXPR_BODY");
  IElementType EXPR_HEAD = new PrologElementType("EXPR_HEAD");
  IElementType IDENT = new PrologElementType("IDENT");
  IElementType LIST_CONSTRUCTOR = new PrologElementType("LIST_CONSTRUCTOR");
  IElementType LITERAL = new PrologElementType("LITERAL");
  IElementType LOGICAL_AND = new PrologElementType("LOGICAL_AND");
  IElementType LOGICAL_NOT = new PrologElementType("LOGICAL_NOT");
  IElementType LOGICAL_OR = new PrologElementType("LOGICAL_OR");
  IElementType PARAMETER_LIST = new PrologElementType("PARAMETER_LIST");
  IElementType PREDICATE = new PrologElementType("PREDICATE");
  IElementType PREDICATE_ID = new PrologElementType("PREDICATE_ID");

  IElementType ARITH_EVAL = new PrologTokenType("is");
  IElementType ATOM_ID = new PrologTokenType("atom_id");
  IElementType COMMA = new PrologTokenType(",");
  IElementType COMMENT = new PrologTokenType("comment");
  IElementType CONST_ID = new PrologTokenType("const_id");
  IElementType DOT = new PrologTokenType(".");
  IElementType EXPAND = new PrologTokenType("-->");
  IElementType FLOAT = new PrologTokenType("float");
  IElementType INTEGER = new PrologTokenType("integer");
  IElementType LB = new PrologTokenType("[");
  IElementType LBR = new PrologTokenType("{");
  IElementType LIST_CONS = new PrologTokenType("|");
  IElementType LP = new PrologTokenType("(");
  IElementType NOT = new PrologTokenType("/-");
  IElementType OPERATOR_ID = new PrologTokenType("operator_id");
  IElementType RB = new PrologTokenType("]");
  IElementType RBR = new PrologTokenType("}");
  IElementType RP = new PrologTokenType(")");
  IElementType SEMI = new PrologTokenType(";");
  IElementType STRING = new PrologTokenType("string");
  IElementType UNIFY = new PrologTokenType(":-");
  IElementType WILDCARD = new PrologTokenType("_");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARITHMETIC_EVAL) {
        return new PrologArithmeticEvalImpl(node);
      }
      else if (type == COMMON_VAL) {
        return new PrologCommonValImpl(node);
      }
      else if (type == EQUIV_BINARY) {
        return new PrologEquivBinaryImpl(node);
      }
      else if (type == EXPR_BODY) {
        return new PrologExprBodyImpl(node);
      }
      else if (type == EXPR_HEAD) {
        return new PrologExprHeadImpl(node);
      }
      else if (type == IDENT) {
        return new PrologIdentImpl(node);
      }
      else if (type == LIST_CONSTRUCTOR) {
        return new PrologListConstructorImpl(node);
      }
      else if (type == LITERAL) {
        return new PrologLiteralImpl(node);
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
      else if (type == PREDICATE_ID) {
        return new PrologPredicateIdImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
