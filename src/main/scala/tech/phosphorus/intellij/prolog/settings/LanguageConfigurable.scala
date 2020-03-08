package tech.phosphorus.intellij.prolog.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.TextComponentAccessor
import javax.swing.JComponent

class LanguageConfigurable extends SearchableConfigurable {

  val configurableGUI = new PrologLanguageConfigurableGUI

  override def getDisplayName: String = "Prolog"

  override def createComponent(): JComponent = {
    configurableGUI.toolchainLocation.setText(PrologStatePersistence.getInstance().getState.toolchain)
    configurableGUI.toolchainLocation
      .addBrowseFolderListener("Choose path to swipl", null, null
        , FileChooserDescriptorFactory.createSingleFolderDescriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT)
    configurableGUI.getRootPanel
  }

  override def apply(): Unit = {
    PrologStatePersistence.getInstance().loadState(new PrologState(configurableGUI.toolchainLocation.getText))
  }

  override def getId: String = "PrologLanguageConfigurable"

  override def isModified: Boolean =
    PrologStatePersistence.getInstance().getState.toolchain != configurableGUI.toolchainLocation.getText
}
