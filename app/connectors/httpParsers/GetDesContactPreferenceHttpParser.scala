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
import models.ContactPreferenceModel
import play.api.http.Status._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object GetDesContactPreferenceHttpParser {

  type Response = Either[ErrorResponse, ContactPreferenceModel]

  implicit object GetDesContactPreferenceHttpReads extends HttpReads[Response] {

    override def read(method: String, url: String, response: HttpResponse): Response = {
      response.status match {
        case OK => response.json.validate[ContactPreferenceModel].fold(
          invalid => {
            Logger.warn(s"[PreferenceConnector][read] Invalid ContactPreference JSON returned from contact-preference: $invalid")
            Left(InvalidJson)
          },
          valid => Right(valid)
        )
        case NOT_FOUND =>
          Logger.warn(s"[PreferenceConnector][read] No contact preference record could be found")
          Left(NotFound)
        case SERVICE_UNAVAILABLE =>
          Logger.warn(s"[PreferenceConnector][read] contact-preferences reported issues communicating with DES")
          Left(DependentSystemUnavailable)
        case status =>
          Logger.warn(s"[PreferenceConnector][read] Error")
          Left(UnexpectedFailure(status, "Error"))
      }
    }
  }

  sealed trait ErrorResponse {
    val status: Int = INTERNAL_SERVER_ERROR
    val body: String
  }

  object InvalidJson extends ErrorResponse {
    override val body = "Invalid Contact Preference JSON returned from contact-preference"
  }

  object NotFound extends ErrorResponse {
    override val status: Int = NOT_FOUND
    override val body = "No contact preference record could be found"
  }

  object DependentSystemUnavailable extends ErrorResponse {
    override val status: Int = SERVICE_UNAVAILABLE
    override val body = "contact-preferences reported issues communicating with DES"
  }

  case class UnexpectedFailure(override val status: Int, override val body: String) extends ErrorResponse

}
