// This is a generated file. Not intended for manual editing.
package tech.phosphorus.intellij.prolog.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PrologLogicalNot extends PsiElement {

  @NotNull
  List<PrologIdent> getIdentList();

  @NotNull
  List<PrologLogicalAnd> getLogicalAndList();

  @NotNull
  List<PrologLogicalNot> getLogicalNotList();

  @NotNull
  List<PrologLogicalOr> getLogicalOrList();

  @NotNull
  List<PrologPredicate> getPredicateList();

}
