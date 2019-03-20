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
import connectors.httpParsers.StorePreferenceHttpParser.{Response => StoreResponse, _}
import javax.inject.{Inject, Singleton}
import models.{ContactPreferenceModel, Preference}
import play.api.Logger
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PreferenceConnector @Inject()(val http: HttpClient, implicit val appConfig: AppConfig) {

  private[connectors] val preferenceUrl = (id: String) => s"${appConfig.contactPreferencesUrl}/$id"

  def storeJourneyPreference(id: String, preference: Preference)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[StoreResponse] = {
    Logger.debug(s"[PreferenceConnector][storeJourneyPreference] Calling backend to store preference for JourneyID: $id\n${preferenceUrl(id)}")
    http.PUT(preferenceUrl(id), ContactPreferenceModel(preference)).recover {
      case e =>
        Logger.error(s"[PreferenceConnector][storeJourneyPreference] Unexpected Error: ${e.getMessage}")
        Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, s"Unexpected Error: ${e.getMessage}"))
    }
  }

}
