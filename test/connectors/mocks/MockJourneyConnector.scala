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

import connectors.JourneyConnector
import connectors.httpParsers.JourneyHttpParser.Response
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestUtils

import scala.concurrent.{ExecutionContext, Future}

trait MockJourneyConnector extends MockFactory with TestUtils {

  lazy val connector = mock[JourneyConnector]

  def mockStartSetJourney(id: String)(response: Response): Unit = {
    (connector.startSetJourney(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(id, *, *)
      .returns(Future.successful(response))
  }

  def mockStartSetJourneyFailed(id: String): Unit = {
    (connector.startSetJourney(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(id, *, *)
      .returns(Future.failed(new Exception))
  }

  def mockStartUpdateJourney(id: String)(response: Response): Unit = {
    (connector.startUpdateJourney(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(id, *, *)
      .returns(Future.successful(response))
  }

  def mockStartUpdateJourneyFailed(id: String): Unit = {
    (connector.startUpdateJourney(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(id, *, *)
      .returns(Future.failed(new Exception))
  }

}
