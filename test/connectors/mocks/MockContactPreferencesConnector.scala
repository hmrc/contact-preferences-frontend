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

package connectors.mocks

import connectors.ContactPreferencesConnector
import connectors.httpParsers.StoreContactPreferenceHttpParser.{Response => StoreResponse}
import connectors.httpParsers.GetContactPreferenceHttpParser.{Response => GetResponse}
import connectors.httpParsers.UpdateContactPreferenceHttpParser.{Response => UpdateResponse}
import models.{Preference, RegimeModel}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtils

import scala.concurrent.{ExecutionContext, Future}

trait MockContactPreferencesConnector extends MockFactory with TestUtils {

  lazy val connector: ContactPreferencesConnector = mock[ContactPreferencesConnector]

  def mockStoreContactPreference(id: String, preference: Preference)(response: StoreResponse): Unit = {
    (connector.storeContactPreference(_: String, _: Preference)(_: HeaderCarrier, _: ExecutionContext))
      .expects(id, preference, *, *)
      .returns(Future.successful(response))
  }

  def mockGetContactPreference(regime: RegimeModel)(response: GetResponse): Unit = {
    (connector.getContactPreference(_: RegimeModel)(_: HeaderCarrier, _: ExecutionContext))
      .expects(regime, *, *)
      .returns(Future.successful(response))
  }

  def mockGetContactPreferenceFailed(regime: RegimeModel): Unit = {
    (connector.getContactPreference(_: RegimeModel)(_: HeaderCarrier, _: ExecutionContext))
      .expects(regime, *, *)
      .returns(Future.failed(new Exception))
  }

  def mockUpdateContactPreference(regime: RegimeModel, preference: Preference)(response: UpdateResponse): Unit = {
    (connector.updateContactPreference(_: RegimeModel, _: Preference)(_: HeaderCarrier, _: ExecutionContext))
      .expects(regime, preference, *, *)
      .returns(Future.successful(response))
  }

}
