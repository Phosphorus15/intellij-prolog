package tech.phosphorus.intellij.prolog.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.{FileViewProvider, PsiElement, ResolveState}
import com.intellij.psi.scope.PsiScopeProcessor
import tech.phosphorus.intellij.prolog.PrologLanguage

class PrologPsiFile(viewProvider: FileViewProvider) extends PsiFileBase(viewProvider, PrologLanguage.INSTANCE) {
  override def getFileType: FileType = new PrologFileType


  // this is important for recursive declarations' locating
  override def processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement, place: PsiElement): Boolean = {
    println("processing declaration for psi files")
    Stream.iterate(this.getLastChild)(_.getPrevSibling).takeWhile(_ != null).foreach(println(_))
    Stream.iterate(this.getLastChild)(_.getPrevSibling).takeWhile(_ != null)
      .forall(_.processDeclarations(processor, state, lastParent, place))
  }
}
