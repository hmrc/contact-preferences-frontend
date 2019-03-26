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
import play.api.http.Status._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object UpdateContactPreferenceHttpParser {

  type Response = Either[ErrorResponse, SuccessResponse]

  implicit object UpdateContactPreferenceHttpReads extends HttpReads[Response] {

    override def read(method: String, url: String, response: HttpResponse): Response = {
      response.status match {
        case NO_CONTENT => Right(Success)
        case BAD_REQUEST => Left(InvalidPreferencePayload)
        case UNAUTHORIZED => Left(Unauthenticated)
        case FORBIDDEN => Left(Forbidden)
        case SERVICE_UNAVAILABLE =>
          Logger.warn(s"[UpdateContactPreferenceHttpReads][read] contact-preferences reported issues communicating with DES")
          Left(DependentSystemUnavailable)
        case status => Left(UnexpectedFailure(status, response.body))
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

  object Unauthenticated extends ErrorResponse {
    override val status: Int = UNAUTHORIZED
    override val body = "The contact-preferences service indicated that the request was unauthenticated"
  }


  object DependentSystemUnavailable extends ErrorResponse {
    override val status: Int = SERVICE_UNAVAILABLE
    override val body = "contact-preferences reported issues communicating with DES"
  }

  object Forbidden extends ErrorResponse {
    override val status: Int = FORBIDDEN
    override val body = "The contact-preferences service indicated that the request was authenticated but not authorised to store the preference"
  }

  case class UnexpectedFailure(override val status: Int, override val body: String) extends ErrorResponse

}
