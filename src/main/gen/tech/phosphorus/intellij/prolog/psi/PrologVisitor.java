// This is a generated file. Not intended for manual editing.
package tech.phosphorus.intellij.prolog.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public class PrologVisitor extends PsiElementVisitor {

  public void visitArithmeticEval(@NotNull PrologArithmeticEval o) {
    visitPsiElement(o);
  }

  public void visitCommonPredicate(@NotNull PrologCommonPredicate o) {
    visitPsiElement(o);
  }

  public void visitCommonVal(@NotNull PrologCommonVal o) {
    visitPsiElement(o);
  }

  public void visitEquivBinary(@NotNull PrologEquivBinary o) {
    visitPsiElement(o);
  }

  public void visitExprBody(@NotNull PrologExprBody o) {
    visitPsiElement(o);
  }

  public void visitExprHead(@NotNull PrologExprHead o) {
    visitPsiElement(o);
  }

  public void visitIdent(@NotNull PrologIdent o) {
    visitPsiElement(o);
  }

  public void visitListConstructor(@NotNull PrologListConstructor o) {
    visitPsiElement(o);
  }

  public void visitLiteral(@NotNull PrologLiteral o) {
    visitPsiElement(o);
  }

  public void visitLogicalAnd(@NotNull PrologLogicalAnd o) {
    visitPsiElement(o);
  }

  public void visitLogicalNot(@NotNull PrologLogicalNot o) {
    visitPsiElement(o);
  }

  public void visitLogicalOr(@NotNull PrologLogicalOr o) {
    visitPsiElement(o);
  }

  public void visitParameterList(@NotNull PrologParameterList o) {
    visitPsiElement(o);
  }

  public void visitPredicate(@NotNull PrologPredicate o) {
    visitPsiElement(o);
  }

  public void visitPredicateId(@NotNull PrologPredicateId o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitRefPredicateId(@NotNull PrologRefPredicateId o) {
    visitPsiElement(o);
  }

  public void visitToplevelExpr(@NotNull PrologToplevelExpr o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitTrailingExpr(@NotNull PrologTrailingExpr o) {
    visitPsiElement(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
