package tech.phosphorus.intellij.prolog.project

import java.util

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.IndexableSetContributor

class PrologIndexableSetContributor extends IndexableSetContributor {
  override def getAdditionalRootsToIndex: util.Set[VirtualFile] = {
    //TODO inject stdlib
  }
}
