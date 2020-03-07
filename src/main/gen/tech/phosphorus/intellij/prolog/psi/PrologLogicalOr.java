// This is a generated file. Not intended for manual editing.
package tech.phosphorus.intellij.prolog.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PrologLogicalOr extends PsiElement {

  @NotNull
  List<PrologCommonVal> getCommonValList();

  @NotNull
  List<PrologEquivBinary> getEquivBinaryList();

  @Nullable
  PrologLogicalNot getLogicalNot();

  @NotNull
  List<PrologLogicalOr> getLogicalOrList();

}
