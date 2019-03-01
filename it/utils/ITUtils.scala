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
import play.api.libs.json.Writes
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import uk.gov.hmrc.play.test.UnitSpec

trait ITUtils extends UnitSpec
  with GuiceOneServerPerSuite
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with WiremockHelper
  with CustomMatchers {

  lazy val ws = app.injector.instanceOf[WSClient]

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }


  def get[T](uri: String): WSResponse = {
    await(
      buildClient(uri).get
    )
  }

  def post[T](uri: String)(body: T)(implicit writes: Writes[T]): WSResponse = {
    await(
      buildClient(uri)
        .withHeaders(
          "Content-Type" -> "application/json",
          "X-Session-ID" -> "123456-session"
        )
        .post(writes.writes(body).toString())
    )
  }

  def buildClient(path: String): WSRequest = ws.url(s"http://localhost:$port/contact-preferences$path").withFollowRedirects(false)

}

