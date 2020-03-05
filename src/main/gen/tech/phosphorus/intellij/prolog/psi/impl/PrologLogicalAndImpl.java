// This is a generated file. Not intended for manual editing.
package tech.phosphorus.intellij.prolog.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static tech.phosphorus.intellij.prolog.psi.PrologTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import tech.phosphorus.intellij.prolog.psi.*;

public class PrologLogicalAndImpl extends ASTWrapperPsiElement implements PrologLogicalAnd {

  public PrologLogicalAndImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PrologVisitor visitor) {
    visitor.visitLogicalAnd(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PrologVisitor) accept((PrologVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<PrologIdent> getIdentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PrologIdent.class);
  }

  @Override
  @NotNull
  public List<PrologLogicalAnd> getLogicalAndList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PrologLogicalAnd.class);
  }

  @Override
  @NotNull
  public List<PrologLogicalNot> getLogicalNotList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PrologLogicalNot.class);
  }

  @Override
  @NotNull
  public List<PrologLogicalOr> getLogicalOrList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PrologLogicalOr.class);
  }

  @Override
  @NotNull
  public List<PrologPredicate> getPredicateList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PrologPredicate.class);
  }

}
