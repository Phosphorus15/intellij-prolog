package tech.phosphorus.intellij.prolog.toolchain

import java.io.{File, FileFilter}
import java.nio.file.{Files, Path, Paths}

import tech.phosphorus.intellij.prolog.FunctionalImplicits._
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.{LocalFileSystem, VirtualFile}
import org.jetbrains.annotations.Contract
import tech.phosphorus.intellij.prolog.settings.{PrologState, PrologStatePersistence}

import scala.collection.mutable
import scala.language.implicitConversions

// Currently there's only swi support
class PrologToolchain(val location: Path, var library: Path = null) {

  def validate(): Boolean =
    Files.isExecutable(executablePath)

  def validateLibrary(): Boolean =
    Files.isDirectory(stdlibPath)

  // Update custom library location from persistence
  def updateLibrary(): Boolean = {
    val newLib = PrologStatePersistence.getInstance().getState.stdLibrary
    if(newLib != null) {
      library = Paths.get(newLib)
    }
    validateLibrary()
  }

  protected def validateLibraryRaw(): Boolean = {
    val newLib = PrologStatePersistence.getInstance().getState.stdLibrary
    newLib != null && Files.isDirectory(Paths.get(newLib))
  }

  def executablePath: Path = {
    location.resolve(if (SystemInfo.isWindows) "swipl.exe" else "swipl")
  }

  def stdlibPath: Path = {
    Option(library).getOrElse(location.resolve("../library").toRealPath())
  }

  def loadStdlib: Option[VirtualFile] = if (validateLibrary()) {
    val fs = LocalFileSystem.getInstance()
    Some(fs.refreshAndFindFileByPath(stdlibPath.toString))
  } else None

  def getSpec: (String, String) = {
    val format = ".* (\\d+\\.\\d+\\.\\d+) for (.*)".r
    val format(version, arch) = new GeneralCommandLine(executablePath.toString).withParameters("--version")
      .captureOutput().getStdout.trim
    (version, arch)
  }

  lazy val specs: (String, String) = getSpec

  lazy val version: String = {
    specs._1
  }

  lazy val arch: String = {
    specs._2
  }

  override def toString: String = f"$version-$arch"

  def description(): String = location.toString

}

object PrologToolchain {
  // have to use this annoying implicit
  implicit def func2FileFilter(f: File => Boolean): FileFilter =
    new FileFilter {
      override def accept(pathname: File): Boolean = f(pathname)
    }

  type Suggestion = mutable.ArrayBuilder[File] => mutable.ArrayBuilder[File]

  val pathSuggestions: Array[Suggestion] = Array(suggestUnix, suggestWindows, suggestPath)

  /** Note that this function changes persistent value */
  @Contract(pure = false) def instanceToolchain(): String = {
    val location = PrologToolchain.suggestToolchainFromPersistence() match {
      case Some(toolchain) => toolchain.location.toString
      case None =>
        PrologToolchain.suggestValidToolchain() match {
          case Some(toolchain) =>
            toolchain.location.toString
          case None => PrologStatePersistence.getInstance().getState.toolchain
        }
    }
    // re-insure that the toolchain was preserved in persistent component
    PrologStatePersistence.getInstance().loadState(new PrologState(location))
    location
  }

  /** Note that this function changes persistent value */
  @Contract(pure = false) def instanceLibrary(toolchainLocation: String): String = {
    if(toolchainLocation == null || toolchainLocation.trim.isEmpty) return ""
    val toolchainVanilla = new PrologToolchain(Paths.get(toolchainLocation))
    if(toolchainVanilla.validate()) {
      if(toolchainVanilla.validateLibraryRaw()) return Option(toolchainVanilla.library).map(_.toString).getOrElse("")
      PrologStatePersistence.getInstance().loadState(new PrologState(toolchainLocation, toolchainVanilla.stdlibPath.toString))
      toolchainVanilla.stdlibPath.toString
    } else ""
  }

  def suggestValidToolchain(): Option[PrologToolchain]
  = suggestPaths().map(_.toPath).map(new PrologToolchain(_)).find(_.validate())

  def suggestToolchainFromPersistence(): Option[PrologToolchain] =
    Option(PrologStatePersistence.getInstance().getState.toolchain)
      .filterNot(_.isEmpty).map(Paths.get(_)).filter(Files.exists(_))
      .map(new PrologToolchain(_)).filter(_.validate())

  def suggestPaths(): Array[File]
  = pathSuggestions.foldLeft((x => x): Suggestion)(_ compose _)(Array.newBuilder).result()

  private def suggestPath(builder: mutable.ArrayBuilder[File])
  = builder ++= Option(System.getenv("PATH")).filterNot(_.isEmpty).getOrElse("").split(File.pathSeparator)
    .filterNot(_.isEmpty).map(new File(_)).filter(_.exists())

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
