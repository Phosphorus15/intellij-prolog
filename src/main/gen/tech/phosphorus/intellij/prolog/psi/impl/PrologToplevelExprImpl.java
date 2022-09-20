// This is a generated file. Not intended for manual editing.
package tech.phosphorus.intellij.prolog.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static tech.phosphorus.intellij.prolog.psi.PrologTypes.*;
import tech.phosphorus.intellij.prolog.psi.PrologTopLevelDeclarationMixin;
import tech.phosphorus.intellij.prolog.psi.*;

public class PrologToplevelExprImpl extends PrologTopLevelDeclarationMixin implements PrologToplevelExpr {

  public PrologToplevelExprImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PrologVisitor visitor) {
    visitor.visitToplevelExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PrologVisitor) accept((PrologVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PrologExprBody getExprBody() {
    return PsiTreeUtil.getChildOfType(this, PrologExprBody.class);
  }

  @Override
  @NotNull
  public PrologExprHead getExprHead() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, PrologExprHead.class));
  }

}
