// This is a generated file. Not intended for manual editing.
package tech.phosphorus.intellij.prolog.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface PrologEquivBinary extends PsiElement {

  @Nullable
  PrologArithmeticEval getArithmeticEval();

  @Nullable
  PrologLogicalAnd getLogicalAnd();

  @Nullable
  PrologLogicalNot getLogicalNot();

  @Nullable
  PrologRuntimeEval getRuntimeEval();

}
