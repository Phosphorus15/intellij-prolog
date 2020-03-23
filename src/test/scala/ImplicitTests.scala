import org.junit.{Assert, Test}
import tech.phosphorus.intellij.prolog.FunctionalImplicits._

@Test
class ImplicitTests extends Assert{
  @Test
  def nullObject(): Unit = {
    localTest(null)
  }

  def localTest(str: String): Unit = {
    assert(!str.isDefined)
    assert(str.isNull)
  }
}
