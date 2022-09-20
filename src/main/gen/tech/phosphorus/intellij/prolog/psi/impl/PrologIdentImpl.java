// This is a generated file. Not intended for manual editing.
package tech.phosphorus.intellij.prolog.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static tech.phosphorus.intellij.prolog.psi.PrologTypes.*;
import tech.phosphorus.intellij.prolog.psi.PrologAtomReferenceMixin;
import tech.phosphorus.intellij.prolog.psi.*;

public class PrologIdentImpl extends PrologAtomReferenceMixin implements PrologIdent {

  public PrologIdentImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PrologVisitor visitor) {
    visitor.visitIdent(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PrologVisitor) accept((PrologVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getAtomId() {
    return findChildByType(ATOM_ID);
  }

  @Override
  @Nullable
  public PsiElement getConstId() {
    return findChildByType(CONST_ID);
  }

}
