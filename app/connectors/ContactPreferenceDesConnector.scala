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
import connectors.httpParsers.GetDesContactPreferenceHttpParser.{Response => GetDesResponse, _}
import javax.inject.{Inject, Singleton}
import models.RegimeModel
import play.api.Logger
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ContactPreferenceDesConnector @Inject()(val http: HttpClient, implicit val appConfig: AppConfig){

  private[connectors] val contactPreferenceDesUrl = (regimeModel: RegimeModel) =>
    s"${appConfig.contactPreferencesUrl}/${regimeModel.getType}/${regimeModel.getId}/${regimeModel.getValue}"

  def getContactPreference(regime: RegimeModel)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[GetDesResponse] = {
    http.GET(contactPreferenceDesUrl(regime)).recover{
      case e =>
        Logger.error(s"[ContactPreferenceDESConnector][getContactPreference] Unexpected Error: ${e.getMessage}")
        Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, s"Unexpected Error: ${e.getMessage}"))
    }
  }
}
