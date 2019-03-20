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

import assets.JourneyTestConstants._
import connectors.httpParsers.JourneyHttpParser._
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import _root_.utils.TestUtils


class JourneyHttpParserSpec extends TestUtils {

  "JourneyHttpParser.JourneyHttpRead" should {

    "return a Journey model when given a valid Json model and status is OK (200)" in {

      val actualResult = JourneyHttpRead.read("","",HttpResponse(Status.OK, Some(journeyJsonMax)))
      val expectedResult = Right(journeyModelMax)

      actualResult shouldBe expectedResult
    }

    "return a NotFound Error when status is Not Found (404)" in {

      val actualResult = JourneyHttpRead.read("","",HttpResponse(Status.NOT_FOUND))
      val expectedResult = Left(NotFound)

      actualResult shouldBe expectedResult
    }

    "return a DependentSystemsUnavailable Error when status is Service Unavailable (503)" in {

      val actualResult = JourneyHttpRead.read("","",HttpResponse(Status.SERVICE_UNAVAILABLE))
      val expectedResult = Left(DependentSystemUnavailable)

      actualResult shouldBe expectedResult
    }

    "return an InvalidJson Error when status returned is OK (200) but JSON is invalid" in {

      val actualResult = JourneyHttpRead.read("","",HttpResponse(Status.OK, Some(Json.obj())))
      val expectedResult = Left(InvalidJson)

      actualResult shouldBe expectedResult
    }

    "return a UnexpectedError when status is not in (200,404,503)" in {

      val actualResult = JourneyHttpRead.read("","",HttpResponse(Status.INTERNAL_SERVER_ERROR))
      val expectedResult = Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, s"http status code ${Status.INTERNAL_SERVER_ERROR} returned returned from contact-preferences"))

      actualResult shouldBe expectedResult
    }
  }


  "JourneyHttpParser.ErrorResponse" when {

    "extended as a concrete InvalidJson object" should {

      "have the status ISE (500)" in {
        InvalidJson.status shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "have the body 'Invalid JSON returned from contact-preferences'" in {
        InvalidJson.body shouldBe "Invalid JSON returned from contact-preferences"
      }
    }

    "extended as a concrete NotFound object" should {

      "have the status Not Found (404)" in {
        NotFound.status shouldBe Status.NOT_FOUND
      }

      "have the body 'No journey record could be found for journey ID supplied'" in {
        NotFound.body shouldBe "No journey record could be found for journey ID supplied"
      }
    }

    "extended as a concrete DependentSystemUnavailable object" should {

      "have the status Service Unavailable (503)" in {
        DependentSystemUnavailable.status shouldBe Status.SERVICE_UNAVAILABLE
      }

      "have the body 'contact-preferences reported issues communicating with the MongoDB repository'" in {
        DependentSystemUnavailable.body shouldBe "contact-preferences reported issues communicating with the MongoDB repository"
      }
    }
  }
}
