package parser

import com.intellij.testFramework.ParsingTestCase
import tech.phosphorus.intellij.prolog.PrologParserDefinition

class PrologParsingTestCaseBase extends ParsingTestCase("", "pl", new PrologParserDefinition){
  override def getTestDataPath: String = "src/test/testData"

  def testBasicParserTest(): Unit = {
    doTest(true)
  }

  def testSingleBootstrapTest(): Unit = {
    doTest(true)
  }

  override def includeRanges(): Boolean = true

  override def skipSpaces(): Boolean = false
}
