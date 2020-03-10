package tech.phosphorus.intellij.prolog.inspector

import java.nio.file.Paths

import com.intellij.execution.configurations.GeneralCommandLine
import tech.phosphorus.intellij.prolog.FunctionalImplicits._
import tech.phosphorus.intellij.prolog.toolchain.PrologToolchain

class SwiPrologLinter {

  lazy val toolchain = new PrologToolchain(Paths.get(PrologToolchain.instanceToolchain()))

  def lintFile(path: String): String = {
    if (toolchain.validate()) {
      new GeneralCommandLine(toolchain.executablePath.toString)
        .withParameters("-g", "halt", path)
        .captureOutput().getStderr
    } else {
      ""
    }
  }

}
