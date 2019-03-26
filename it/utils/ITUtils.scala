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

package utils

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.data.Form
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Writes
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import uk.gov.hmrc.play.test.UnitSpec
import play.api.{Application, Environment, Mode}


trait ITUtils extends UnitSpec
  with GuiceOneServerPerSuite
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with WiremockHelper
  with CustomMatchers {

  lazy val ws = app.injector.instanceOf[WSClient]

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build

  def config: Map[String, String] = Map(
    "application.router" -> "testOnlyDoNotUseInAppConf.Routes",
    "microservice.services.auth.host" -> WiremockHelper.wiremockHost,
    "microservice.services.auth.port" -> WiremockHelper.wiremockPort.toString,
    "microservice.services.contact-preferences.host" -> WiremockHelper.wiremockHost,
    "microservice.services.contact-preferences.port" -> WiremockHelper.wiremockPort.toString,
    "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck"
  )

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }


  def get[T](uri: String, cookies: Map[String, String] = Map.empty): WSResponse = {
    await(
      buildClient(uri)
        .withHeaders(
          HeaderNames.COOKIE -> SessionCookieBaker.bakeSessionCookie(cookies), "Csrf-Token" -> "nocheck"
        )
        .get
    )
  }

  def post(uri: String, cookies: Map[String, String] = Map.empty)(body:  Map[String, Seq[String]]): WSResponse = {
    await(
      buildClient(uri)
        .withHeaders(
          HeaderNames.COOKIE -> SessionCookieBaker.bakeSessionCookie(cookies), "Csrf-Token" -> "nocheck"
        )
        .post(body)
    )
  }

  def buildClient(path: String): WSRequest = ws.url(s"http://localhost:$port/contact-preferences$path").withFollowRedirects(false)

  def toFormData[T](form: Form[T], data: T): Map[String, Seq[String]] =
    form.fill(data).data map { case (k, v) => k -> Seq(v) }
}

