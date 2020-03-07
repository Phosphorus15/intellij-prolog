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

public class PrologCommonValImpl extends ASTWrapperPsiElement implements PrologCommonVal {

  public PrologCommonValImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PrologVisitor visitor) {
    visitor.visitCommonVal(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PrologVisitor) accept((PrologVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PrologIdent getIdent() {
    return findChildByClass(PrologIdent.class);
  }

  @Override
  @Nullable
  public PrologListConstructor getListConstructor() {
    return findChildByClass(PrologListConstructor.class);
  }

  @Override
  @Nullable
  public PrologLiteral getLiteral() {
    return findChildByClass(PrologLiteral.class);
  }

  @Override
  @Nullable
  public PrologPredicate getPredicate() {
    return findChildByClass(PrologPredicate.class);
  }

}
