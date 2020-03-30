package tech.phosphorus.intellij.prolog.settings

import java.nio.file.Paths

import com.intellij.openapi.Disposable
import tech.phosphorus.intellij.prolog.RunnableImplicits._
import com.intellij.openapi.application.{ApplicationManager, ModalityState}
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.progress.{ProgressIndicator, ProgressManager, Task}
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.util.Disposer
import com.intellij.ui.{DocumentAdapter, JBColor}
import com.intellij.util.Alarm
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import tech.phosphorus.intellij.prolog.toolchain.PrologToolchain

class LanguageConfigurable extends SearchableConfigurable {

  val configurableGUI = new PrologLanguageConfigurableGUI

  override def getDisplayName: String = "Prolog"

  // Setting up settings component, try to grab toolchain here
  override def createComponent(): JComponent = {
    val location: String = PrologToolchain.instanceToolchain()
    val library: String = PrologToolchain.instanceLibrary(location)
    configurableGUI.toolchainLocation.setText(location)
    configurableGUI.stdlibLocation.setText(library)
    configurableGUI.toolchainLocation
      .addBrowseFolderListener("Choose path to swipl", null, null
        , FileChooserDescriptorFactory.createSingleFolderDescriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT)
    configurableGUI.stdlibLocation
      .addBrowseFolderListener("Choose path to stdlib (commonly the `library` dir)", null, null
        , FileChooserDescriptorFactory.createSingleFolderDescriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT)
    configurableGUI.toolchainLocation.getChildComponent.getDocument.addDocumentListener(
      new DocumentAdapter {
        override def textChanged(documentEvent: DocumentEvent): Unit = refreshToolchainInfo()
      }
    )
    refreshToolchainInfo() // manual refresh
    configurableGUI.getRootPanel
  }

  def refreshToolchainInfo(): Unit = {
    ProgressManager.getInstance().run(new Task.Backgroundable(null, "Updating toolchain version") {
      override def run(progressIndicator: ProgressIndicator): Unit = {
        val toolchain = new PrologToolchain(Paths.get(configurableGUI.toolchainLocation.getText))
        val application = ApplicationManager.getApplication
        if (toolchain.validate()) {
          val descriptor = toolchain.toString
          application.invokeLater(() => {
            configurableGUI.toolchainStatus.setText(descriptor)
            configurableGUI.toolchainStatus.setForeground(JBColor.foreground())
          }, ModalityState.any())
        } else {
          application.invokeLater(() => {
            configurableGUI.toolchainStatus.setText("N/A")
            configurableGUI.toolchainStatus.setForeground(JBColor.RED)
          }, ModalityState.any())
        }
      }
    })
  }

  override def apply(): Unit = {
    PrologStatePersistence.getInstance().loadState(new PrologState(configurableGUI.toolchainLocation.getText, configurableGUI.stdlibLocation.getText))
  }

  override def getId: String = "PrologLanguageConfigurable"

  override def isModified: Boolean = {
    val state = PrologStatePersistence.getInstance().getState
    state.stdLibrary != configurableGUI.stdlibLocation.getText || state.toolchain != configurableGUI.toolchainLocation.getText
  }
}
