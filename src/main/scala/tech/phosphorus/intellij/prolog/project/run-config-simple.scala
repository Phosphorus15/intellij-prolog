package tech.phosphorus.intellij.prolog.project

import com.intellij.execution.actions.{ConfigurationContext, LazyRunConfigurationProducer, RunConfigurationProducer}
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.lineMarker.{ExecutorAction, RunLineMarkerContributor}
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.{AnAction, CommonDataKeys}
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import tech.phosphorus.intellij.prolog.FunctionalImplicits.Nullable
import tech.phosphorus.intellij.prolog.psi.{PrologFileType, PrologPsiUtil, PrologRefPredicateId, PrologTrailingExpr, PrologTypes}

class PrologSimpleRunConfigurationProducer extends LazyRunConfigurationProducer[PrologRunConfiguration] {

  private def getVirtualFile(configurationContext: ConfigurationContext) =
    configurationContext.getPsiLocation.asOption().map(_.getContainingFile).fold(
      configurationContext.getDataContext.getData(CommonDataKeys.VIRTUAL_FILE)
    )(f => f.getVirtualFile).asOption()

  override def setupConfigurationFromContext(t: PrologRunConfiguration, configurationContext: ConfigurationContext, ref: Ref[PsiElement]): Boolean = {
    val file = getVirtualFile(configurationContext)
    val fileType = file.map(_.getFileType)
    if (file.isEmpty || fileType.isEmpty || fileType.get != PrologFileType.INSTANCE) {
      return false
    }
    file.foreach(file => {
      t.targetFile = file.getPath
      t.setName(file.getPresentableName)
    })
    true
  }

  override def getConfigurationFactory: ConfigurationFactory = (new PrologRunConfigurationType).factories(0)

  override def isConfigurationFromContext(t: PrologRunConfiguration, configurationContext: ConfigurationContext): Boolean = {
    val file = getVirtualFile(configurationContext)
    file.fold(false)(f => t.targetFile == f.getPath)
  }
}

class PrologSimpleRunLineMarkerContributor extends RunLineMarkerContributor {
  override def getInfo(psiElement: PsiElement): RunLineMarkerContributor.Info = {
    if (psiElement.getNode.getElementType == PrologTypes.CONST_ID && psiElement.getParent != null
      && psiElement.getParent.isInstanceOf[PrologRefPredicateId] && psiElement.getParent.getText == "initialization") {
      if (PrologPsiUtil.findParentWith(psiElement.getParent, _.isInstanceOf[PrologTrailingExpr]).isDefined) {
        val actions = ExecutorAction.getActions(0)
        return new RunLineMarkerContributor.Info(AllIcons.RunConfigurations.TestState.Run
          , actions
          , new java.util.function.Function[PsiElement, String] {
            override def apply(t: PsiElement): String = actions.map(getText(_, t)).foldLeft("")(_ + "\n" + _)
          })
      }
    }
    null
  }

  def getText(action: AnAction, t: PsiElement): String = {
    RunLineMarkerContributor.getText(action, t)
  }
}
