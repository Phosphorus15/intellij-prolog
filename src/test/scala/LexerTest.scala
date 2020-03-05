import java.io.{BufferedReader, ByteArrayInputStream, InputStreamReader}

import org.junit.{Assert, Test}
import tech.phosphorus.intellij.prolog.PrologLexer

@Test
class LexerTest extends Assert {

  @Test
  def simpleLexical() : Unit = {
    val lexer = new PrologLexer(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(("Alpha" +
      "").getBytes))))
    while(true) {
      val item = lexer.advance()
      if (item != null) {
        println(item)
      } else return
    }
  }

}
