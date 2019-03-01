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

import models.{ContactPreferenceModel, Preference}
import play.api.Logger
import play.api.http.Status
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object PreferenceHttpParser {

  type Response = Either[ErrorResponse, Preference]

  object PreferenceHttpRead extends HttpReads[Response] {

    override def read(method: String, url: String, response: HttpResponse): Response = {
      response.status match {
        case Status.OK => response.json.validate[ContactPreferenceModel] match {
          case JsSuccess(value, _) => Right(value.preference)
          case JsError(error) => Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, error.head._2.head.message))
        }
        case status => {
          Logger.warn(s"[PreferenceHttpRead][read] http status code $status returned returned from contact-preferences")
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