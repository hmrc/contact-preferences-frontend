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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.Preference
import play.api.http.Status._
import play.api.libs.json.JsValue
import utils.WireMockMethods

object ContactPreferencesStub extends WireMockMethods {

  private val journeyUri = (id: String) => s"/contact-preferences/journey/$id"

  def getJourneySuccess(id: String)(response: JsValue): StubMapping =
    when(method = GET, uri = journeyUri(id)).thenReturn(status = OK, body = response)

  def getJourneyFailed(id: String): StubMapping =
    when(method = GET, uri = journeyUri(id)).thenReturn(status = SERVICE_UNAVAILABLE)


  private val preferenceUri = (id: String) => s"/contact-preferences/$id"

  def storePreferenceSuccess(id: String): StubMapping = {
    when(method = PUT, uri = preferenceUri(id)).thenReturn(status = NO_CONTENT)
  }

  def storePreferenceFailed(id: String): StubMapping = {
    when(method = PUT, uri = preferenceUri(id)).thenReturn(status = NOT_FOUND)
  }
}
