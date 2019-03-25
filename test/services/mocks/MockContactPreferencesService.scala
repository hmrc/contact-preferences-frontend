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

package services.mocks

import connectors.httpParsers.StoreContactPreferenceHttpParser.{Response => StoreResponse}
import connectors.httpParsers.GetContactPreferenceHttpParser.{Response => GetResponse}
import models.{Preference, RegimeModel}
import org.scalamock.scalatest.MockFactory
import services.ContactPreferencesService
import uk.gov.hmrc.http.HeaderCarrier
import _root_.utils.TestUtils

import scala.concurrent.{ExecutionContext, Future}


trait MockContactPreferencesService extends MockFactory with TestUtils {

  lazy val mockContactPreferencesService: ContactPreferencesService = mock[ContactPreferencesService]

  def mockStoreJourneyPreference(id: String, preference: Preference)(response: StoreResponse): Unit = {
    (mockContactPreferencesService.storeJourneyPreference(_: String, _: Preference)(_: HeaderCarrier, _: ExecutionContext))
      .expects(id, preference, *, *)
      .returns(Future.successful(response))
  }

  def mockGetContactPreference(regime: RegimeModel)(response: GetResponse): Unit = {
    (mockContactPreferencesService.getContactPreference(_: RegimeModel)(_: HeaderCarrier, _: ExecutionContext))
      .expects(regime, *, *)
      .returns(Future.successful(response))
  }

}
