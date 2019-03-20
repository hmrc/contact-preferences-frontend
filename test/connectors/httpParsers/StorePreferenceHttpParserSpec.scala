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

import connectors.httpParsers.StorePreferenceHttpParser._
import play.api.http.Status
import uk.gov.hmrc.http.HttpResponse
import _root_.utils.TestUtils


class StorePreferenceHttpParserSpec extends TestUtils {

  "StorePreferenceHttpParser.StorePreferenceHttpRead" should {

    "return Success object when status is NO_CONTENT (204)" in {

      val actualResult = StorePreferenceHttpRead.read("","", HttpResponse(Status.NO_CONTENT))
      val expectedResult = Right(Success)

      actualResult shouldBe expectedResult
    }

    "return a NotFound Error when status is Not Found (404)" in {

      val actualResult = StorePreferenceHttpRead.read("","", HttpResponse(Status.NOT_FOUND))
      val expectedResult = Left(NotFound)

      actualResult shouldBe expectedResult
    }

    "return a Forbidden Error when status is Forbidden (403)" in {

      val actualResult = StorePreferenceHttpRead.read("","", HttpResponse(Status.FORBIDDEN))
      val expectedResult = Left(Forbidden)

      actualResult shouldBe expectedResult
    }

    "return a Unauthenticated Error when status is Unauthorised (401)" in {

      val actualResult = StorePreferenceHttpRead.read("","", HttpResponse(Status.UNAUTHORIZED))
      val expectedResult = Left(Unauthenticated)

      actualResult shouldBe expectedResult
    }

    "return a InvalidPreferencePayload Error when status is BadRequest (400)" in {

      val actualResult = StorePreferenceHttpRead.read("","", HttpResponse(Status.BAD_REQUEST))
      val expectedResult = Left(InvalidPreferencePayload)

      actualResult shouldBe expectedResult
    }

    "return a UnexpectedError when status is 500)" in {

      val actualResult = StorePreferenceHttpRead.read("","", HttpResponse(Status.INTERNAL_SERVER_ERROR))
      val expectedResult = Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, s"http status code ${Status.INTERNAL_SERVER_ERROR} returned returned from contact-preferences"))

      actualResult shouldBe expectedResult
    }
  }


  "JourneyHttpParser.ErrorResponse" when {

    "extended as a concrete NotFound object" should {

      "have the status Not Found (404)" in {
        NotFound.status shouldBe Status.NOT_FOUND
      }

      "have the body 'No journey record could be found for journey ID supplied'" in {
        NotFound.body shouldBe "No journey record could be found for journey ID supplied"
      }
    }

    "extended as a concrete Forbidden object" should {

      "have the status Forbidden (403)" in {
        Forbidden.status shouldBe Status.FORBIDDEN
      }

      "have the body 'The contact-preferences service indicated that the request was authenticated but not authorised to store the preference'" in {
        Forbidden.body shouldBe
          "The contact-preferences service indicated that the request was authenticated but not authorised to store the preference"
      }
    }

    "extended as a concrete Unauthenticated object" should {

      "have the status Unauthorised (401)" in {
        Unauthenticated.status shouldBe Status.UNAUTHORIZED
      }

      "have the body 'The contact-preferences service indicated that the request was unauthenticated'" in {
        Unauthenticated.body shouldBe "The contact-preferences service indicated that the request was unauthenticated"
      }
    }

    "extended as a concrete InvalidPreferencePayload object" should {

      "have the status BadRequest (400)" in {
        InvalidPreferencePayload.status shouldBe Status.BAD_REQUEST
      }

      "have the body 'The contact-preferences service indicated that the Contact Preference Payload was invalid'" in {
        InvalidPreferencePayload.body shouldBe "The contact-preferences service indicated that the Contact Preference Payload was invalid"
      }
    }
  }
}
