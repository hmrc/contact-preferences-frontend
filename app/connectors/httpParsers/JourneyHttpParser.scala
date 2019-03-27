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

package connectors.httpParsers

import models._
import play.api.Logger
import play.api.http.Status
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, SERVICE_UNAVAILABLE, UNAUTHORIZED}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object JourneyHttpParser {

  type Response = Either[ErrorResponse, Journey]

  implicit object JourneyHttpRead extends HttpReads[Response] {

    override def read(method: String, url: String, response: HttpResponse): Response = {
      response.status match {
        case Status.OK => {
          response.json.validate[Journey](Journey.format).fold(
            invalid => {
              Logger.debug(s"[JourneyHttpParser][read] Invalid JSON returned from DES: $invalid")
              Logger.warn(s"[JourneyHttpParser][read] Invalid JSON returned from DES")
              Left(InvalidJson)
            },
            valid => Right(valid)
          )
        }
        case NOT_FOUND => {
          Logger.warn(s"[JourneyHttpParser][read] ${NotFound.body}")
          Left(NotFound)
        }
        case UNAUTHORIZED => {
          Logger.warn(s"[JourneyHttpParser][read] ${NotFound.body}")
          Left(Unauthorised)
        }
        case SERVICE_UNAVAILABLE => {
          Logger.warn(s"[JourneyHttpParser][read] ${DependentSystemUnavailable.body}")
          Left(DependentSystemUnavailable)
        }
        case status => {
          Logger.warn(s"[JourneyHttpParser][read] http status code $status returned returned from contact-preferences")
          Left(UnexpectedFailure(status, s"http status code $status returned returned from contact-preferences"))
        }
      }
    }
  }

  sealed trait ErrorResponse {
    val status: Int = INTERNAL_SERVER_ERROR
    val body: String
  }

  object InvalidJson extends ErrorResponse {
    override val body = "Invalid JSON returned from contact-preferences"
  }

  object NotFound extends ErrorResponse {
    override val status: Int = NOT_FOUND
    override val body = "No journey record could be found for journey ID supplied"
  }

  object Unauthorised extends ErrorResponse {
    override val status: Int = UNAUTHORIZED
    override val body = "contact-preferences returned an uanthorised status "
  }

  object DependentSystemUnavailable extends ErrorResponse {
    override val status: Int = SERVICE_UNAVAILABLE
    override val body = "contact-preferences reported issues communicating with the MongoDB repository"
  }

  case class UnexpectedFailure(override val status: Int, override val body: String) extends ErrorResponse
}