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

import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.Journey
import play.api.Logger
import play.api.http.Status
import testOnly.connectors.httpParsers.PreferenceHttpParser.PreferenceHttpRead
import testOnly.connectors.httpParsers.SimulateJourneySubmitHttpParser._
import testOnly.connectors.httpParsers.{PreferenceHttpParser, SimulateJourneySubmitHttpParser}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ContactPreferencesConnector @Inject()(val http: HttpClient, implicit val appConfig: AppConfig) {

  def startJourney(journey: Journey)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[SimulateJourneySubmitHttpParser.Response] = {
    val url = s"${appConfig.contactPreferencesUrl}/journey"
    Logger.debug(s"[ContactPreferencesConnector][startJourney] Calling backend to start journey $url")
    http.POST(url, journey)(implicitly, SimulateJourneyHttpRead, hc, ec).recover {
      case e =>
        Logger.error(s"[ContactPreferencesConnector][startJourney] Unexpected Error: ${e.getMessage}")
        Left(SimulateJourneySubmitHttpParser.UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, s"Unexpected Error: ${e.getMessage}"))
    }
  }

  def getPreference(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[PreferenceHttpParser.Response] = {
    val url: String => String = id => s"${appConfig.contactPreferencesUrl}/$id"
    Logger.debug(s"[ContactPreferencesConnector][getPreference] Calling backend to start journey $url")
    http.GET(url(id))(PreferenceHttpRead, hc, ec).recover {
      case e =>
        Logger.error(s"[ContactPreferencesConnector][getPreference] Unexpected Error: ${e.getMessage}")
        Left(PreferenceHttpParser.UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, s"Unexpected Error: ${e.getMessage}"))
    }
  }

}
