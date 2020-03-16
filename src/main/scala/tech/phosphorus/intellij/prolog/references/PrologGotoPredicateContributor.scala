package tech.phosphorus.intellij.prolog.references

import com.intellij.navigation.{ChooseByNameContributor, NavigationItem}
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import tech.phosphorus.intellij.prolog.psi.PrologPredicateId
import tech.phosphorus.intellij.prolog.toolchain.PrologPredicateStubIndex

class PrologGotoPredicateContributor extends ChooseByNameContributor{
  override def getNames(project: Project, includeNonProjectItems: Boolean): Array[String] = {
    PrologPredicateStubIndex.getAllKeys(project)
  }

  override def getItemsByName(name: String, pattern: String, project: Project, includeNonProjectItems: Boolean): Array[NavigationItem] = {
    val items = StubIndex.getElements(PrologPredicateStubIndex.KEY, name, project, GlobalSearchScope.allScope(project), classOf[PrologPredicateId])
    items.toArray(Array[NavigationItem]())
  }
}
