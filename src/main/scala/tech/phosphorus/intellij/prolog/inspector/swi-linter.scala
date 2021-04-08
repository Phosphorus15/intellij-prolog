package tech.phosphorus.intellij.prolog.inspector

import com.intellij.execution.configurations.GeneralCommandLine
import tech.phosphorus.intellij.prolog.FunctionalImplicits._
import tech.phosphorus.intellij.prolog.toolchain.PrologToolchain

import scala.collection.mutable
import scala.util.matching.Regex

class ReportType

final case class Error() extends ReportType

final case class Warning() extends ReportType

sealed class LinterReport(val ty: ReportType, val line: Int, val location: Option[Int], var message: String)

object SwiPrologLinter {

  // FIXME Multi-line problem
  final val locationPattern: Regex = "(Warning|ERROR):\\s+.+?:(\\d+?):((\\d+?):)?\\s+(.*)".r

}

class SwiPrologLinter(val toolchain: PrologToolchain) {

  def canLint: Boolean = toolchain.validate()

  def lintFile(path: String, workdir: String = null): Array[LinterReport] = {
    if (canLint) {
      val ttyOut = new GeneralCommandLine(toolchain.executablePath.toString)
        .withWorkDirectory(workdir)
        .withParameters("-g", "halt", "-l", path)
        .captureOutput()
      val lines = ttyOut.getStderrLines.toArray(Array[String]())
      val lints = mutable.MutableList[LinterReport]()
      var afterInitialize = false
      for (line <- lines) {
        SwiPrologLinter.locationPattern.findFirstMatchIn(line) match {
          case Some(pattern) =>
            val list = pattern.subgroups
            lints += new LinterReport(list.headOption match {
              case Some("ERROR") => new Error
              case _ => new Warning
            }, list(1).toInt, Option(list(3)).map(_.toInt), list(4))
            afterInitialize = true
          case None =>
            if (lints.isEmpty && !afterInitialize) {
              val pattern = "(Warning|ERROR):\\s+(.+)".r.findFirstMatchIn(line)
              assert(pattern.isDefined)
              pattern.foreach { pattern =>
                val list = pattern.subgroups
                // we have no CATs so no combination here :sad:
                (list.headOption, list.lastOption) match {
                  case (Some(head), Some(last)) =>
                    lints += new LinterReport(head match {
                      case "ERROR" => new Error
                      case _ => new Warning
                    }, 1, None, last)
                  case _ =>
                }
              }
            } else {
              assert(lints.nonEmpty) // a error should be filed
              val pattern = "(Warning|ERROR):\\s+(.+)".r.findFirstMatchIn(line)
              lints.lastOption.foreach { lastLint =>
                pattern match {
                  case Some(pattern) =>
                    pattern.subgroups.lastOption.foreach { last =>
                      lastLint.message += f"\n${last}"
                    }
                  case None =>
                    lastLint.message += f"\n ${line.trim}"
                }
              }
            }
        }
      }
      lints.toArray
    } else {
      Array()
    }
  }

}
