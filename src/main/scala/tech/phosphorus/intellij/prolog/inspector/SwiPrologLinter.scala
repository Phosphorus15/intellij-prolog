package tech.phosphorus.intellij.prolog.inspector

import com.intellij.execution.configurations.GeneralCommandLine
import tech.phosphorus.intellij.prolog.FunctionalImplicits._
import tech.phosphorus.intellij.prolog.toolchain.PrologToolchain

import scala.util.matching.Regex

class ReportType

final case class Error() extends ReportType

final case class Warning() extends ReportType

sealed class LinterReport(val ty: ReportType, val line: Int, val location: Option[Int], val message: String)

object SwiPrologLinter {

  final val locationPattern: Regex = "(Warning|ERROR):\\s+.+?:(\\d+?):((\\d+?):)?\\s+(.*)".r

}

class SwiPrologLinter(val toolchain: PrologToolchain) {

  def canLint(): Boolean = toolchain.validate()

  var err : java.util.List[String] = _
  var out : java.util.List[String] = _

  def lintFile(path: String): Array[LinterReport] = {
    if (canLint()) {
      println(path)
      val ttyOut = new GeneralCommandLine(toolchain.executablePath.toString)
        .withParameters("-g", "halt", "-l", path)
        .captureOutput()
      SwiPrologLinter.locationPattern.findAllMatchIn(ttyOut.getStderr)
          .map({ v =>
            val list = v.subgroups
            new LinterReport(list.head match {
              case "ERROR" => new Error
              case _ => new Warning
            }, list(1).toInt, Option(list(3)).map(_.toInt), list(4))
          }).toArray
    } else {
      Array()
    }
  }

}
