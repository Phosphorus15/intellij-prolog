package tech.phosphorus.intellij.prolog.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.{FileType, FileTypeConsumer, FileTypeFactory, LanguageFileType}
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import javax.swing.{Icon, ImageIcon}
import tech.phosphorus.intellij.prolog.PrologLanguage

class PrologPsiFile(viewProvider: FileViewProvider) extends PsiFileBase(viewProvider, PrologLanguage.INSTANCE) {
  override def getFileType: FileType = new PrologFileType
}
