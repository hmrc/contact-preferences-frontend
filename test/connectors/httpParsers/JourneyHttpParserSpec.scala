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

import connectors.httpParsers.JourneyHttpParser.JourneyHttpRead
import models.ErrorModel
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.TestUtils
import assets.JourneyTestConstants._


class JourneyHttpParserSpec extends TestUtils {

  "JourneyHttpParser" should {

    "return a Journey model when given a valid Json model" in {

      val actualResult = JourneyHttpRead.read("","",HttpResponse(Status.OK, Some(journeyJsonMax)))
      val expectedResult = Right(journeyModelMax)

      actualResult shouldBe expectedResult
    }

    "return an Error when status returned is not Ok" in {

      val actualResult = JourneyHttpRead.read("","",HttpResponse(Status.OK, Some(Json.obj())))
      val expectedResult = Left(ErrorModel(
        Status.INTERNAL_SERVER_ERROR,
        "Invalid Json returned from contact-preferences"
      ))

      actualResult shouldBe expectedResult
    }

    "return an Error when incorrect json is returned" in {

      val actualResult = JourneyHttpRead.read("","",HttpResponse(Status.SERVICE_UNAVAILABLE, Some(journeyJsonMax)))
      val expectedResult = Left(ErrorModel(
        Status.SERVICE_UNAVAILABLE,
        s"http status code ${Status.SERVICE_UNAVAILABLE} returned returned from contact-preferences"
      ))

      actualResult shouldBe expectedResult
    }
  }
}
