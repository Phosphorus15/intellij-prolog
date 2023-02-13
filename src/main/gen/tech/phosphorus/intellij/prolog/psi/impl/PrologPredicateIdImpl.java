// This is a generated file. Not intended for manual editing.
package tech.phosphorus.intellij.prolog.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static tech.phosphorus.intellij.prolog.psi.PrologTypes.*;
import tech.phosphorus.intellij.prolog.psi.PrologDeclarationMixin;
import tech.phosphorus.intellij.prolog.psi.*;
import tech.phosphorus.intellij.prolog.toolchain.PrologPredicateStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;

public class PrologPredicateIdImpl extends PrologDeclarationMixin implements PrologPredicateId {

  public PrologPredicateIdImpl(PrologPredicateStub stub, IStubElementType<? extends StubElement<?>, ? extends PsiElement> type, ASTNode node) {
    super(stub, type, node);
  }

  public PrologPredicateIdImpl(PrologPredicateStub stub, IStubElementType<? extends StubElement<?>, ? extends PsiElement> type) {
    super(stub, type);
  }

  public PrologPredicateIdImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PrologVisitor visitor) {
    visitor.visitPredicateId(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PrologVisitor) accept((PrologVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getConstId() {
    return notNullChild(findChildByType(CONST_ID));
  }

}
