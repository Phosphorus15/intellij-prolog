package tech.phosphorus.intellij.prolog.settings

import com.intellij.openapi.components.{PersistentStateComponent, ServiceManager, State, Storage}

@State(name = "PrologStatePersistence", storages = Array(new Storage("prolog.xml")))
class PrologStatePersistence extends PersistentStateComponent[PrologState] {

  override def getState: PrologState = {
    PrologStatePersistence.state
  }

  override def loadState(t: PrologState): Unit = {
    PrologStatePersistence.state = t
  }

}

object PrologStatePersistence {
  var state = new PrologState

  def getInstance(): PrologStatePersistence = {
    ServiceManager.getService(classOf[PrologStatePersistence])
  }
}
