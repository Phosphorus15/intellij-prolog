package tech.phosphorus.intellij.prolog

import java.awt.Component
import java.io.{ObjectInputStream, PrintWriter, StringWriter}
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util
import java.util.Base64
import java.util.function.Consumer

import com.intellij.diagnostic.{AbstractMessage, IdeErrorsDialog, ReportMessages}
import com.intellij.ide.DataManager
import com.intellij.ide.plugins.{PluginManager, PluginManagerCore}
import com.intellij.idea.IdeaLogger
import com.intellij.notification.{NotificationListener, NotificationType}
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.diagnostic.SubmittedReportInfo.SubmissionStatus
import com.intellij.openapi.diagnostic.{Attachment, ErrorReportSubmitter, IdeaLoggingEvent, SubmittedReportInfo}
import com.intellij.openapi.progress.{ProgressIndicator, Task}
import com.intellij.openapi.project.Project
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

object AnonymousFeedback {
  val tokenFile = "tech/phosphorus/intellij/prolog/settings/token.bin"
  val gitRepoUser = "Phosphorus15"
  val gitRepo = "intellij-prolog"
  val issueLabel = "bug"

  private implicit class FunctorAsPipeline[T](val t: T) extends AnyVal {
    def |>[R](f: T => R): R = f(t)
  }

  private implicit class LetInContext[T](val t: T) extends AnyVal {
    def letIn(f: T => Unit): T = {
      if (t != null) f(t)
      t
    }
  }

  //
  //  def sendFeedback(environment: Map[String, String]): SubmittedReportInfo = {
  //
  //  }

  private val initVector = "RandomInitVector"
  private val key = "GitHubErrorToken"

  private def decrypt(file: URL): String = {
    Cipher.getInstance("AES/CBC/PKCS5PADDING") |> (cipher => {
      cipher.init(Cipher.DECRYPT_MODE,
        new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"),
        new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8)))
      return new String(cipher.doFinal(Base64.getDecoder.decode(file.openStream() |> (it => {
        new ObjectInputStream(it).readObject().asInstanceOf[String]
      }))))
    })
  }

  private def encrypt(value: String): String = {
    Cipher.getInstance("AES/CBC/PKCS5PADDING") |> (cipher => {
      cipher.init(Cipher.ENCRYPT_MODE,
        new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"),
        new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8)))
      return Base64.getEncoder.encodeToString(cipher.doFinal(value.getBytes()))
    })
  }

  class GitHubErrorReport extends ErrorReportSubmitter {
    override def getReportActionText: String = "submit prolog plugin error"

    def doSubmit(event: IdeaLoggingEvent, parent: Component, callback: Consumer[SubmittedReportInfo], desc: String): Unit = {
      val dataContext = DataManager.getInstance().getDataContext(parent)
      val bean = new GitHubErrorBean(
        event.getThrowable,
        IdeaLogger.ourLastActionId,
        desc,
        event.getMessage)
      IdeErrorsDialog.findPluginId(event.getThrowable).letIn { pluginId =>
        PluginManagerCore.getPlugin(pluginId).letIn { ideaPluginDescriptor =>
          if (!ideaPluginDescriptor.isBundled) {
            bean.pluginName = ideaPluginDescriptor.getName
            bean.pluginVersion = ideaPluginDescriptor.getVersion
          }
        }
      }
      event.getData match {
        case msg: AbstractMessage => bean.attachments = msg.getIncludedAttachments.toArray(Array[Attachment]()).toList
      }
      val project = CommonDataKeys.PROJECT.getData(dataContext)
    }

    class CallbackWithNotification(
                                    private val consumer: Consumer[SubmittedReportInfo],
                                    private val project: Project) extends Consumer[SubmittedReportInfo] {
      override def accept(reportInfo: SubmittedReportInfo) {
        consumer.accept(reportInfo)
        if (reportInfo.getStatus == SubmissionStatus.FAILED) ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT,
          reportInfo.getLinkText, NotificationType.ERROR, null).setImportant(false).notify(project)
        else ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT, reportInfo.getLinkText,
          NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER).setImportant(false).notify(project)
      }
    }

  }

  class GitHubErrorBean(throwable: Throwable,
                        val lastAction: String,
                        val description: String,
                        val message: String) {
    val exceptionHash: String = util.Arrays.hashCode(throwable.getStackTrace.toArray[Object]).toString
    val stackTrace: String = (new StringWriter() letIn (writer => throwable.printStackTrace(new PrintWriter(writer)))).toString


    var pluginName = ""
    var pluginVersion = ""
    var attachments: List[Attachment] = List()
  }

  private class AnonymousFeedbackTask(project: Project, title: String, canBeCancelled: Boolean,
                                      private val params: Map[String, String],
                                      private val callback: Consumer[SubmittedReportInfo]) extends Task.Backgroundable(project, title, canBeCancelled) {
    override def run(indicator: ProgressIndicator) {
      indicator.setIndeterminate(true)
      // TODO     callback.accept(AnonymousFeedback.sendFeedback(params))
    }
  }

}
