import java.io.File

import org.junit.{Assert, Test}
import tech.phosphorus.intellij.prolog.toolchain.PrologToolchain

@Test
class ToolchainTest extends Assert{
  @Test
  def detectValidToolchain(): Unit = {
    PrologToolchain.suggestPaths().map((f: File) => new PrologToolchain(f.toPath))
      .filter(_.validate()).map(_.version).foreach(println(_))
  }
}
