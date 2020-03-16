package tech.phosphorus.intellij.prolog.settings

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAware

class PrologShowSettingsAction extends AnAction("Configure") with DumbAware {

  override def update(e: AnActionEvent) {
    super.update(e)
    e.getPresentation.setEnabledAndVisible(e.getProject != null)
  }

  override def actionPerformed(anActionEvent: AnActionEvent): Unit = {
    ShowSettingsUtil.getInstance().showSettingsDialog(anActionEvent.getProject, "Prolog")
  }
}
