/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import java.util.Base64

import javax.inject.{Inject, Singleton}
import play.api.Mode.Mode
import play.api.mvc.{Call, Request}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.binders.ContinueUrl
import uk.gov.hmrc.play.config.ServicesConfig

@Singleton
class AppConfig @Inject()(val runModeConfiguration: Configuration, val environment: Environment) extends ServicesConfig {
  override protected def mode: Mode = environment.mode

  private val contactHost: String = getString(ConfigKeys.contactFrontendService)
  private val contactFormServiceIdentifier: String = "MSCC"

  lazy val analyticsToken: String = getString(ConfigKeys.googleAnalyticsToken)
  lazy val analyticsHost: String = getString(ConfigKeys.googleAnalyticsToken)

  lazy val reportAProblemPartialUrl: String = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl: String = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  private def continueUrl(implicit request: Request[_]) = ContinueUrl(host + request.uri).encodedUrl

  def feedbackUrl(implicit request: Request[_]): String = s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier" +
    s"&backUrl=$continueUrl"

  private lazy val signInBaseUrl: String = getString(ConfigKeys.signInBaseUrl)
  private lazy val signInOrigin = getString(ConfigKeys.appName)
  def signInUrl(implicit request: Request[_]): String = s"$signInBaseUrl?continue=$continueUrl&origin=$signInOrigin"
  private lazy val governmentGatewayHost: String = getString(ConfigKeys.governmentGatewayHost)
  private lazy val timeOutRedirectUrl = host + controllers.routes.SessionTimeoutController.timeout().url
  lazy val timeOutSignOutUrl = s"$governmentGatewayHost/gg/sign-out?continue=$timeOutRedirectUrl"
  lazy val signOutUrl = s"$governmentGatewayHost/gg/sign-out"


  lazy val host: String = getString(ConfigKeys.host)

  private def whitelistConfig(key: String): Seq[String] =
    Some(new String(Base64.getDecoder.decode(runModeConfiguration.getString(key)
      .getOrElse("")), "UTF-8")).map(_.split(",")).getOrElse(Array.empty).toSeq

  lazy val shutterPage: String = getString(ConfigKeys.shutterPage)
  lazy val whitelistIps: Seq[String] = whitelistConfig(ConfigKeys.whitelistIps)
  lazy val whitelistExcludedPaths: Seq[Call] = whitelistConfig(ConfigKeys.whitelistExcludedPaths).map(path => Call("GET", path))
  lazy val whiteListEnabled: Boolean = getBoolean(ConfigKeys.whitelistEnabled)

  lazy val contactPreferencesUrl: String = s"${baseUrl(ConfigKeys.contactPreferencesService)}/contact-preferences"

  lazy val timeoutCountdown: Int = getInt(ConfigKeys.timeoutCountdown)
  lazy val timeoutPeriod: Int = getInt(ConfigKeys.timeoutPeriod)

}
