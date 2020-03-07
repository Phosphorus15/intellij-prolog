package tech.phosphorus.intellij.prolog.psi

import com.intellij.psi.tree.IElementType
import tech.phosphorus.intellij.prolog.PrologLanguage

class PrologTokenType(debugName : String) extends IElementType(debugName, PrologLanguage.INSTANCE)

class PrologElementType(debugName : String) extends IElementType(debugName, PrologLanguage.INSTANCE)
