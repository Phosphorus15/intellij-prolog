/*
 * Copyright (c) 2020 Phosphorus15
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package tech.phosphorus.intellij.prolog

import java.awt.Component
import java.io.{BufferedReader, IOException, InputStreamReader, ObjectInputStream, PrintWriter, StringWriter}
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.util
import java.util.function.Consumer
import java.util.{Base64, Collections}

import com.intellij.diagnostic.{AbstractMessage, IdeErrorsDialog, ReportMessages}
import com.intellij.ide.DataManager
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.idea.IdeaLogger
import com.intellij.notification.{NotificationListener, NotificationType}
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.diagnostic.SubmittedReportInfo.SubmissionStatus
import com.intellij.openapi.diagnostic._
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.progress.{EmptyProgressIndicator, ProgressIndicator, ProgressManager, Task}
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.IssueService
import org.eclipse.egit.github.core.{Issue, Label, RepositoryId}

import scala.collection.JavaConversions._
import scala.collection.mutable

object AnonymousFeedback {
  val tokenFile = "token.bin"
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

  /**
   * Makes a connection to GitHub. Checks if there is an issue that is a duplicate and based on this, creates either a
   * new issue or comments on the duplicate (if the user provided additional information).
   *
   * @param environmentDetails Information collected by [getKeyValuePairs]
   * @return The report info that is then used in [GitHubErrorReporter] to show the user a balloon with the link
   *         of the created issue.
   */
  private def sendFeedback(environmentDetails: mutable.Map[String, String]): SubmittedReportInfo = {
    val logger = Logger.getInstance(getClass.getName)
    try {
      val resource: URL = getClass.getClassLoader.getResource(tokenFile)
      if (resource == null) {
        logger.info("Could not find token file")
        throw new IOException("Could not decrypt access token")
      }
      val gitAccessToken = decrypt(resource)
      val client = new GitHubClient()
      client.setOAuth2Token(gitAccessToken)
      val repoID = RepositoryId.createFromUrl("https://github.com/Phosphorus15/intellij-prolog")
      val issueService = new IssueService(client)
      var newGibHubIssue = createNewGibHubIssue(environmentDetails)
      val duplicate = findFirstDuplicate(newGibHubIssue.getTitle, issueService, repoID)
      var isNewIssue = true
      if (duplicate.isDefined) {
        issueService.createComment(repoID, duplicate.get.getNumber, generateGitHubIssueBody(environmentDetails, false).toString())
        newGibHubIssue = duplicate.get
        isNewIssue = false
      } else newGibHubIssue = issueService.createIssue(repoID, newGibHubIssue)
      new SubmittedReportInfo(newGibHubIssue.getHtmlUrl, newGibHubIssue.getHtmlUrl,
        if (isNewIssue) SubmissionStatus.NEW_ISSUE else SubmissionStatus.DUPLICATE)
    } catch {
      case e: IOException =>
        e.printStackTrace()
        new SubmittedReportInfo(null,
          "Github connection failed.",
          SubmissionStatus.FAILED)
    }
  }

  private def findFirstDuplicate(uniqueTitle: String, service: IssueService, repo: RepositoryId): Option[Issue] = {
    val searchParameters = new util.HashMap[String, String](2)
    searchParameters.put(IssueService.FILTER_STATE, IssueService.STATE_OPEN)
    service.pageIssues(repo, searchParameters).flatten.find(_.getTitle == uniqueTitle)
  }

  private def createNewGibHubIssue(details: mutable.Map[String, String]) = new Issue().letIn(issue => {
    val errorMessage = details.remove("error.message")
    issue.setTitle("[auto-generated" + s":${details.remove("error.hash").getOrElse("")}] " + (if (errorMessage.isDefined) errorMessage.get else "Plugin Error"))
    details.put("title", issue.getTitle)
    issue.setBody(generateGitHubIssueBody(details, includeStacktrace = true).toString())
    issue.setLabels(Collections.singletonList(new Label().letIn(label => {
      label.setName(issueLabel)
    })))
  })

  private def generateGitHubIssueBody(details: mutable.Map[String, String], includeStacktrace: Boolean) =
    new StringBuilder().letIn(builder => {
      val errorDescription = details.remove("error.description")
      val stackTrace = details.remove("error.stacktrace").getOrElse("")
      if (errorDescription.nonEmpty) builder.append(errorDescription).append("\n\n----------------------\n")
      for ((key, value) <- details) builder.append("- ").append(key).append(": ").append(value).append("\n")
      if (includeStacktrace) builder.append("\n```\n").append(stackTrace).append("```\n")
    })

  private val initVector = "RandomInitVector"
  private val key = "GitHubErrorToken"

  private def decrypt(file: URL): String = {
    Cipher.getInstance("AES/CBC/PKCS5PADDING") |> (cipher => {
      cipher.init(Cipher.DECRYPT_MODE,
        new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"),
        new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8)))
      return new String(cipher.doFinal(Base64.getDecoder.decode(file.openStream() |> (it => {
        new BufferedReader(new InputStreamReader(it)).readLine()
      }))))
    })
  }

  def main(args: Array[String]): Unit = {
    if (args.length != 2) return
    Files.write(Paths.get(args(1)), encrypt(args(0)).getBytes())
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
    override def getReportActionText: String = "Submit Issue"

    override def submit(events: Array[IdeaLoggingEvent], info: String, parent: Component, consumer: com.intellij.util.Consumer[SubmittedReportInfo]): Boolean = {
      val event = events.headOption
      if (event.isDefined)
        doSubmit(event.get, parent, new Consumer[SubmittedReportInfo] {
          override def accept(t: SubmittedReportInfo): Unit = consumer.consume(t)
        }, info)
      else false
    }

    def doSubmit(event: IdeaLoggingEvent, parent: Component, callback: Consumer[SubmittedReportInfo], desc: String): Boolean = {
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
      val reportValues = getKeyValuePairs(
        project,
        bean,
        ApplicationInfoEx.getInstanceEx,
        ApplicationNamesInfo.getInstance())
      val notifyingCallback = new CallbackWithNotification(callback, project)
      val task = new AnonymousFeedbackTask(project, "Report GitHub issue", true, reportValues, notifyingCallback)
      if (project == null) task.run(new EmptyProgressIndicator()) else ProgressManager.getInstance().run(task)
      true
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
                                      private val params: mutable.Map[String, String],
                                      private val callback: Consumer[SubmittedReportInfo]) extends Task.Backgroundable(project, title, canBeCancelled) {
    override def run(indicator: ProgressIndicator) {
      indicator.setIndeterminate(true)
      callback.accept(AnonymousFeedback.sendFeedback(params))
    }
  }

  private def getKeyValuePairs(
                                project: Project,
                                error: GitHubErrorBean,
                                appInfo: ApplicationInfoEx,
                                namesInfo: ApplicationNamesInfo): mutable.Map[String, String] = {
    PluginManagerCore.getPlugin(PluginId.findId("tech.phosphorus.intellij-prolog")) |> (plugin => {
      if (error.pluginName.isEmpty) error.pluginName = plugin.getName
      if (error.pluginVersion.isEmpty) error.pluginVersion = plugin.getVersion
    })
    val params = mutable.Map(
      "error.description" -> error.description,
      "Plugin Name" -> error.pluginName,
      "Plugin Version" -> error.pluginVersion,
      "OS Name" -> SystemInfo.OS_NAME,
      "Java Version" -> SystemInfo.JAVA_VERSION,
      "App Name" -> namesInfo.getProductName,
      "App Full Name" -> namesInfo.getFullProductName,
      "App Version name" -> appInfo.getVersionName,
      "Is EAP" -> java.lang.Boolean.toString(appInfo.isEAP),
      "App Build" -> appInfo.getBuild.asString(),
      "App Version" -> appInfo.getFullVersion,
      "Last Action" -> error.lastAction,
      "error.message" -> error.message,
      "error.stacktrace" -> error.stackTrace,
      "error.hash" -> error.exceptionHash)
    for (attachment <- error.attachments)
      params.put("Attachment ${attachment.name}", attachment.getEncodedBytes)
    params
  }

}
