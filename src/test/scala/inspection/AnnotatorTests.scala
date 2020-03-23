package inspection

import com.intellij.testFramework.HeavyPlatformTestCase

class AnnotatorTests extends HeavyPlatformTestCase{
  def singletonTest(): Unit = {
    annotatedWith(null)
  }
}
