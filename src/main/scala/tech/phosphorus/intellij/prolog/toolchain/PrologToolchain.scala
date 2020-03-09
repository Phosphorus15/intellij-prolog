package tech.phosphorus.intellij.prolog.toolchain

import java.io.{File, FileFilter}
import java.nio.file.{Files, Path}

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.{CapturingProcessHandler, ProcessOutput}
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.SystemInfo

import scala.collection.mutable

// Currently there's only swi support
class PrologToolchain(val location: Path) {

  implicit class ExecutedGeneralCommandLine(cmd: GeneralCommandLine) {
    def captureStdOutput(timeout: Int = 1000): ProcessOutput = {
      try {
        new CapturingProcessHandler(cmd).runProcess(timeout)
      } catch {
        case _ : ExecutionException => null
      }
    }
  }

  def validate(): Boolean =
    Files.isExecutable(executablePath)

  protected def executablePath: Path = {
    location.resolve(if (SystemInfo.isWindows) "swipl.exe" else "swipl")
  }

  def getSpec: (String, String) = {
    val format = ".* (\\d+\\.\\d+\\.\\d+) for (.*)".r
    val format(version, arch) = new GeneralCommandLine(executablePath.toString).withParameters("--version")
      .captureStdOutput().getStdout.trim
    (version, arch)
  }

  lazy val version: String = {
    getSpec._1
  }

  lazy val arch: String = {
    getSpec._2
  }

  def description(): String = location.toString

}

object PrologToolchain {
  // have to use this annoying implicit
  implicit def func2PropertyChangeListener(f: File => Boolean): FileFilter =
    new FileFilter {
      override def accept(pathname: File): Boolean = f(pathname)
    }

  type Suggestion = mutable.ArrayBuilder[File] => mutable.ArrayBuilder[File]

  val pathSuggestions: Array[Suggestion] = Array(suggestUnix, suggestWindows, suggestPath)

  def suggestPaths(): Array[File]
  = pathSuggestions.foldLeft((x => x): Suggestion)(_ compose _)(Array.newBuilder).result()

  private def suggestPath(builder: mutable.ArrayBuilder[File])
  = builder ++= Option(System.getenv("PATH")).filterNot(_.isEmpty).getOrElse("").split(File.pathSeparator)
    .filter(!_.isEmpty).map(new File(_)).filter(_.exists())

  private def suggestUnix(builder: mutable.ArrayBuilder[File])
  = if (SystemInfo.isUnix) builder += (new File("/usr/local/bin"), new File("/usr/bin"))
  else builder

  private def suggestWindows(builder: mutable.ArrayBuilder[File])
  = if (SystemInfo.isWindows) {
    val programFiles = new File(System.getenv("ProgramFiles"))
    if (programFiles.exists() && programFiles.isDirectory) {
      builder ++= programFiles.listFiles((x: File) => x.isDirectory)
        .filter(_.getName.toLowerCase.startsWith("swipl"))
        .map(new File(_, "bin")).filter(_.exists())
    }
    else builder
  } else builder

}
