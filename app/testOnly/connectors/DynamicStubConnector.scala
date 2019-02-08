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

package testOnly.connectors

import javax.inject.Inject
import play.api.libs.json.JsValue
import testOnly.TestOnlyAppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class DynamicStubConnector @Inject()(val http: HttpClient,
                                     val appConfig: TestOnlyAppConfig) {

  implicit val reads: HttpReads[HttpResponse] = new HttpReads[HttpResponse] {
    def read(method: String, url: String, response: HttpResponse): HttpResponse = response
  }

  def populateStub(dataModel: JsValue)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    lazy val url = s"${appConfig.dynamicStubUrl}/setup/data"
    http.POST[JsValue, HttpResponse](url, dataModel)
  }

  def clearStub()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    lazy val url = s"${appConfig.dynamicStubUrl}/setup/all-data"
    http.DELETE[HttpResponse](url)
  }

  def populateSchema(schemaModel: JsValue)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    lazy val url = s"${appConfig.dynamicStubUrl}/setup/schema"
    http.POST[JsValue, HttpResponse](url, schemaModel)
  }

  def clearSchema()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    lazy val url = s"${appConfig.dynamicStubUrl}/setup/all-schemas"
    http.DELETE[HttpResponse](url)
  }
}
