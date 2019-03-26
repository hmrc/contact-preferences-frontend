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

package connectors

import config.AppConfig
import connectors.httpParsers.{StoreContactPreferenceHttpParser => StoreHttpParser}
import connectors.httpParsers.{GetContactPreferenceHttpParser => GetHttpParser}
import connectors.httpParsers.{UpdateContactPreferenceHttpParser => UpdateHttpParser}
import javax.inject.{Inject, Singleton}
import models.{ContactPreferenceModel, Preference, RegimeModel}
import play.api.Logger
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ContactPreferencesConnector @Inject()(val http: HttpClient, implicit val appConfig: AppConfig) {

  private[connectors] val storePreferenceUrl = (id: String) => s"${appConfig.contactPreferencesUrl}/$id"

  def storeContactPreference(id: String, preference: Preference)
                            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[StoreHttpParser.Response] = {

    Logger.debug(s"[ContactPreferencesConnector][storeContactPreference] Calling backend to store preference for JourneyID: $id\n${storePreferenceUrl(id)}")
    http.PUT(storePreferenceUrl(id), ContactPreferenceModel(preference))(ContactPreferenceModel.format, StoreHttpParser.StorePreferenceHttpRead, hc, ec)
      .recover {
        case e =>
          Logger.error(s"[ContactPreferencesConnector][storeContactPreference] Unexpected Error: ${e.getMessage}")
          Left(StoreHttpParser.UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, s"Unexpected Error: ${e.getMessage}"))
      }
  }

  private[connectors] val preferenceUrl = (regimeModel: RegimeModel) =>
    s"${appConfig.contactPreferencesUrl}/${regimeModel.getType}/${regimeModel.getId}/${regimeModel.getValue}"

  def getContactPreference(regime: RegimeModel)
                          (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[GetHttpParser.Response] = {

    http.GET(preferenceUrl(regime))(GetHttpParser.GetDesContactPreferenceHttpReads, hc, ec)
      .recover {
        case e =>
          Logger.error(s"[ContactPreferencesConnector][getContactPreference] Unexpected Error: ${e.getMessage}")
          Left(GetHttpParser.UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, s"Unexpected Error: ${e.getMessage}"))
      }
  }

  def updateContactPreference(regime: RegimeModel, preference: Preference)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[UpdateHttpParser.Response] = {

    http.PUT(preferenceUrl(regime), preference)(Preference.writes, UpdateHttpParser.UpdateContactPreferenceHttpReads, hc, ec)
      .recover {
        case e =>
          Logger.error(s"[ContactPreferencesConnector][updateContactPreference] Unexpected Error: ${e.getMessage}")
          Left(UpdateHttpParser.UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, s"Unexpected Error: ${e.getMessage}"))
      }
  }
}
