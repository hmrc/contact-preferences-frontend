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

package testOnly.connectors.httpParsers

import play.api.Logger
import play.api.http.{HeaderNames, Status}
import play.api.http.Status._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object SimulateJourneySubmitHttpParser {

  type Response = Either[ErrorResponse, SuccessResponse]

  object SimulateJourneyHttpRead extends HttpReads[Response] {

    override def read(method: String, url: String, response: HttpResponse): Response = {
      response.status match {
        case Status.CREATED => Right(Success(response.header(HeaderNames.LOCATION).getOrElse("")))
        case status => {
          Logger.warn(s"[PreferenceHttpParser][read] http status code $status returned returned from contact-preferences")
          Left(UnexpectedFailure(status, s"http status code $status returned returned from contact-preferences"))
        }
      }
    }
  }

  sealed trait SuccessResponse
  case class Success(url: String) extends SuccessResponse

  sealed trait ErrorResponse {
    val status: Int = INTERNAL_SERVER_ERROR
    val body: String
  }
  case class UnexpectedFailure(override val status: Int, override val body: String) extends ErrorResponse
}