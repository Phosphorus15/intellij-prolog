package tech.phosphorus.intellij.prolog.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import tech.phosphorus.intellij.prolog.PrologLanguage

class PrologPsiFile(viewProvider: FileViewProvider) extends PsiFileBase(viewProvider, PrologLanguage.INSTANCE) {
  override def getFileType: FileType = new PrologFileType
}
