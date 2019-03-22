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
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import connectors.httpParsers.JourneyHttpParser._
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import play.api.http.Status

@Singleton
class JourneyConnector @Inject()(val http: HttpClient, implicit val appConfig: AppConfig) {

  private[connectors] val journeyUrl = (id: String) => s"${appConfig.contactPreferencesUrl}/journey/$id"

  def getJourney(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Response] = {

    val url = journeyUrl(id)
    Logger.debug(s"[JourneyConnector][startSetJourney] Calling backend to retrieve preference for JourneyID: $id\n$url")
    http.GET(url).recover {
      case e =>
        Logger.error(s"[JourneyConnector][startSetJourney] Unexpected Error: ${e.getMessage}")
        Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, s"Unexpected Error: ${e.getMessage}"))
    }
  }
}
