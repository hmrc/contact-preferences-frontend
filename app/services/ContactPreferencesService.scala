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

package services

import connectors.ContactPreferencesConnector
import connectors.httpParsers.StoreContactPreferenceHttpParser.{Response => StoreResponse}
import connectors.httpParsers.GetContactPreferenceHttpParser.{Response => GetResponse}
import connectors.httpParsers.UpdateContactPreferenceHttpParser.{Response => UpdateResponse}
import javax.inject.{Inject, Singleton}
import models.{Preference, RegimeModel}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ContactPreferencesService @Inject()(preferenceConnector: ContactPreferencesConnector){

  def storeJourneyPreference(id: String, preference: Preference)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[StoreResponse] =
    preferenceConnector.storeContactPreference(id, preference)

  def getContactPreference(regime: RegimeModel)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[GetResponse] =
    preferenceConnector.getContactPreference(regime)

  def updateContactPreference(regime: RegimeModel, preference: Preference)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[UpdateResponse] =
    preferenceConnector.updateContactPreference(regime, preference)
}
