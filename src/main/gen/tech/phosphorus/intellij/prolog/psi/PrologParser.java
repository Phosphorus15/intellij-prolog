// This is a generated file. Not intended for manual editing.
package tech.phosphorus.intellij.prolog.psi;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static tech.phosphorus.intellij.prolog.psi.PrologTypes.*;
import static tech.phosphorus.intellij.prolog.psi.PrologParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PrologParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  /* ********************************************************** */
  // 'is' primary
  public static boolean arithmetic_eval(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_eval")) return false;
    if (!nextTokenIs(b, ARITH_EVAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ARITH_EVAL);
    r = r && primary(b, l + 1);
    exit_section_(b, m, ARITHMETIC_EVAL, r);
    return r;
  }

  /* ********************************************************** */
  // predicate | ident | literal | list_constructor | '_'
  public static boolean common_val(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "common_val")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMON_VAL, "<common val>");
    r = predicate(b, l + 1);
    if (!r) r = ident(b, l + 1);
    if (!r) r = literal(b, l + 1);
    if (!r) r = list_constructor(b, l + 1);
    if (!r) r = consumeToken(b, WILDCARD);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // common_val | '(' common_val ')' | '{' common_val '}'
  static boolean common_val_or_paren(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "common_val_or_paren")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = common_val(b, l + 1);
    if (!r) r = common_val_or_paren_1(b, l + 1);
    if (!r) r = common_val_or_paren_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' common_val ')'
  private static boolean common_val_or_paren_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "common_val_or_paren_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && common_val(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, null, r);
    return r;
  }

  // '{' common_val '}'
  private static boolean common_val_or_paren_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "common_val_or_paren_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBR);
    r = r && common_val(b, l + 1);
    r = r && consumeToken(b, RBR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // logical_not | logical_and | arithmetic_eval
  public static boolean equiv_binary(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equiv_binary")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EQUIV_BINARY, "<equiv binary>");
    r = logical_not(b, l + 1);
    if (!r) r = logical_and(b, l + 1);
    if (!r) r = arithmetic_eval(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // primary
  public static boolean expr_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_body")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_BODY, "<expr body>");
    r = primary(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // predicate
  public static boolean expr_head(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_head")) return false;
    if (!nextTokenIs(b, CONST_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = predicate(b, l + 1);
    exit_section_(b, m, EXPR_HEAD, r);
    return r;
  }

  /* ********************************************************** */
  // const_id | atom_id
  public static boolean ident(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ident")) return false;
    if (!nextTokenIs(b, "<ident>", ATOM_ID, CONST_ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IDENT, "<ident>");
    r = consumeToken(b, CONST_ID);
    if (!r) r = consumeToken(b, ATOM_ID);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' ']' | '[' primary '|' primary ']' | '[' primary ']'
  public static boolean list_constructor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_constructor")) return false;
    if (!nextTokenIs(b, LB)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokens(b, 0, LB, RB);
    if (!r) r = list_constructor_1(b, l + 1);
    if (!r) r = list_constructor_2(b, l + 1);
    exit_section_(b, m, LIST_CONSTRUCTOR, r);
    return r;
  }

  // '[' primary '|' primary ']'
  private static boolean list_constructor_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_constructor_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LB);
    r = r && primary(b, l + 1);
    r = r && consumeToken(b, LIST_CONS);
    r = r && primary(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, null, r);
    return r;
  }

  // '[' primary ']'
  private static boolean list_constructor_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_constructor_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LB);
    r = r && primary(b, l + 1);
    r = r && consumeToken(b, RB);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // string | integer | float
  public static boolean literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL, "<literal>");
    r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, INTEGER);
    if (!r) r = consumeToken(b, FLOAT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ',' primary
  public static boolean logical_and(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logical_and")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && primary(b, l + 1);
    exit_section_(b, m, LOGICAL_AND, r);
    return r;
  }

  /* ********************************************************** */
  // '/-' primary
  public static boolean logical_not(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logical_not")) return false;
    if (!nextTokenIs(b, NOT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NOT);
    r = r && primary(b, l + 1);
    exit_section_(b, m, LOGICAL_NOT, r);
    return r;
  }

  /* ********************************************************** */
  // ';' primary
  public static boolean logical_or(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logical_or")) return false;
    if (!nextTokenIs(b, SEMI)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEMI);
    r = r && primary(b, l + 1);
    exit_section_(b, m, LOGICAL_OR, r);
    return r;
  }

  /* ********************************************************** */
  // '(' primary? ')'
  public static boolean parameter_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list")) return false;
    if (!nextTokenIs(b, LP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LP);
    r = r && parameter_list_1(b, l + 1);
    r = r && consumeToken(b, RP);
    exit_section_(b, m, PARAMETER_LIST, r);
    return r;
  }

  // primary?
  private static boolean parameter_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list_1")) return false;
    primary(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // predicate_id parameter_list?
  public static boolean predicate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "predicate")) return false;
    if (!nextTokenIs(b, CONST_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = predicate_id(b, l + 1);
    r = r && predicate_1(b, l + 1);
    exit_section_(b, m, PREDICATE, r);
    return r;
  }

  // parameter_list?
  private static boolean predicate_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "predicate_1")) return false;
    parameter_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // const_id
  public static boolean predicate_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "predicate_id")) return false;
    if (!nextTokenIs(b, CONST_ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CONST_ID);
    exit_section_(b, m, PREDICATE_ID, r);
    return r;
  }

  /* ********************************************************** */
  // (common_val_or_paren | logical_not | operator_id) (common_val_or_paren | equiv_binary | logical_or  | operator_id)*
  static boolean primary(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primary")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = primary_0(b, l + 1);
    r = r && primary_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // common_val_or_paren | logical_not | operator_id
  private static boolean primary_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primary_0")) return false;
    boolean r;
    r = common_val_or_paren(b, l + 1);
    if (!r) r = logical_not(b, l + 1);
    if (!r) r = consumeToken(b, OPERATOR_ID);
    return r;
  }

  // (common_val_or_paren | equiv_binary | logical_or  | operator_id)*
  private static boolean primary_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primary_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!primary_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "primary_1", c)) break;
    }
    return true;
  }

  // common_val_or_paren | equiv_binary | logical_or  | operator_id
  private static boolean primary_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primary_1_0")) return false;
    boolean r;
    r = common_val_or_paren(b, l + 1);
    if (!r) r = equiv_binary(b, l + 1);
    if (!r) r = logical_or(b, l + 1);
    if (!r) r = consumeToken(b, OPERATOR_ID);
    return r;
  }

  /* ********************************************************** */
  // rule*
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    while (true) {
      int c = current_position_(b);
      if (!rule(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // !<<eof>> ((expr_head ((':-' | '-->') expr_body)?) | (':-' expr_body)) '.'
  static boolean rule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rule_0(b, l + 1);
    r = r && rule_1(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  // !<<eof>>
  private static boolean rule_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !eof(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (expr_head ((':-' | '-->') expr_body)?) | (':-' expr_body)
  private static boolean rule_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rule_1_0(b, l + 1);
    if (!r) r = rule_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr_head ((':-' | '-->') expr_body)?
  private static boolean rule_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr_head(b, l + 1);
    r = r && rule_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ((':-' | '-->') expr_body)?
  private static boolean rule_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_1_0_1")) return false;
    rule_1_0_1_0(b, l + 1);
    return true;
  }

  // (':-' | '-->') expr_body
  private static boolean rule_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rule_1_0_1_0_0(b, l + 1);
    r = r && expr_body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ':-' | '-->'
  private static boolean rule_1_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_1_0_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, UNIFY);
    if (!r) r = consumeToken(b, EXPAND);
    exit_section_(b, m, null, r);
    return r;
  }

  // ':-' expr_body
  private static boolean rule_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, UNIFY);
    r = r && expr_body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

}
