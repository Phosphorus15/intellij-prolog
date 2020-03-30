package tech.phosphorus.intellij.prolog.psi

import com.intellij.psi.tree.IElementType
import tech.phosphorus.intellij.prolog.PrologLanguage
import tech.phosphorus.intellij.prolog.toolchain.PrologStubElementType

class PrologTokenType(debugName: String) extends IElementType(debugName, PrologLanguage.INSTANCE)

class PrologElementType(debugName: String) extends IElementType(debugName, PrologLanguage.INSTANCE)

object PrologElementType {
  def createType(debugName: String): IElementType = {
    debugName match {
      case "PREDICATE_ID" => PrologStubElementType.PREDICATE_ID
      case _ => new PrologElementType(debugName)
    }
  }
}
