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
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object JourneyHttpParser {

  type Response = Either[ErrorModel, Journey]

  implicit object JourneyHttpRead extends HttpReads[Either[ErrorModel, Journey]] {

    override def read(method: String, url: String, response: HttpResponse): Either[ErrorModel, Journey] = {
      response.status match {
        case Status.OK => {
          response.json.validate[Journey](Journey.format).fold(
            invalid => Left(InvalidJson),
            valid => Right(valid)
          )
        }
        case status => Left(ErrorModel(status, s"http status code $status returned returned from contact-preferences"))
      }
    }
  }
}