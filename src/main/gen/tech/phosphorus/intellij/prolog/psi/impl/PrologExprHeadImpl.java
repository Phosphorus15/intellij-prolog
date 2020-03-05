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

public class PrologExprHeadImpl extends ASTWrapperPsiElement implements PrologExprHead {

  public PrologExprHeadImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PrologVisitor visitor) {
    visitor.visitExprHead(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PrologVisitor) accept((PrologVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PrologPredicate getPredicate() {
    return findNotNullChildByClass(PrologPredicate.class);
  }

}
