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

import connectors.JourneyConnector
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import connectors.httpParsers.JourneyHttpParser.Response

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyService @Inject()(journeyConnector: JourneyConnector){

  def startSetJourney(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Response] = {
    journeyConnector.startSetJourney(id)
  }

  def startUpdateJourney(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Response] = {
    journeyConnector.startUpdateJourney(id)
  }
}
