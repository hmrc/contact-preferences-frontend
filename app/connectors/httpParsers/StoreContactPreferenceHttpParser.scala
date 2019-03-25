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

import play.api.Logger
import play.api.http.Status
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, BAD_REQUEST, FORBIDDEN, UNAUTHORIZED}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object StoreContactPreferenceHttpParser {

  type Response = Either[ErrorResponse, SuccessResponse]

  implicit object StorePreferenceHttpRead extends HttpReads[Response] {

    override def read(method: String, url: String, response: HttpResponse): Response = {
      response.status match {
        case Status.NO_CONTENT => Right(Success)
        case Status.BAD_REQUEST => Left(InvalidPreferencePayload)
        case Status.UNAUTHORIZED => Left(Unauthenticated)
        case Status.FORBIDDEN => Left(Forbidden)
        case Status.NOT_FOUND => Left(NotFound)
        case status => {
          Logger.warn(s"[PreferenceHttpParser][read] http status code $status returned returned from contact-preferences")
          Left(UnexpectedFailure(status, s"http status code $status returned returned from contact-preferences"))
        }
      }
    }
  }

  sealed trait SuccessResponse
  object Success extends SuccessResponse

  sealed trait ErrorResponse {
    val status: Int = INTERNAL_SERVER_ERROR
    val body: String
  }

  object InvalidPreferencePayload extends ErrorResponse {
    override val status: Int = BAD_REQUEST
    override val body = "The contact-preferences service indicated that the Contact Preference Payload was invalid"
  }

  object NotFound extends ErrorResponse {
    override val status: Int = NOT_FOUND
    override val body = "No journey record could be found for journey ID supplied"
  }

  object Unauthenticated extends ErrorResponse {
    override val status: Int = UNAUTHORIZED
    override val body = "The contact-preferences service indicated that the request was unauthenticated"
  }

  object Forbidden extends ErrorResponse {
    override val status: Int = FORBIDDEN
    override val body = "The contact-preferences service indicated that the request was authenticated but not authorised to store the preference"
  }

  case class UnexpectedFailure(override val status: Int, override val body: String) extends ErrorResponse
}