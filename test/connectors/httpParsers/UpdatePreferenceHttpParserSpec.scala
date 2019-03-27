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

import _root_.utils.TestUtils
import connectors.httpParsers.UpdateContactPreferenceHttpParser._
import play.api.http.Status
import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.http.HttpResponse


class UpdatePreferenceHttpParserSpec extends TestUtils {

  "UpdateContactPreferenceHttpParser.UpdateContactPreferenceHttpReads" should {

    "return Success object when status is NO_CONTENT (204)" in {

      val actualResult = UpdateContactPreferenceHttpReads.read("","", HttpResponse(Status.NO_CONTENT))
      val expectedResult = Right(Success)

      actualResult shouldBe expectedResult
    }

    "return a Forbidden Error when status is Forbidden (403)" in {

      val actualResult = UpdateContactPreferenceHttpReads.read("","", HttpResponse(Status.FORBIDDEN))
      val expectedResult = Left(Forbidden)

      actualResult shouldBe expectedResult
    }

    "return a Unauthenticated Error when status is Unauthorised (401)" in {

      val actualResult = UpdateContactPreferenceHttpReads.read("","", HttpResponse(Status.UNAUTHORIZED))
      val expectedResult = Left(Unauthenticated)

      actualResult shouldBe expectedResult
    }

    "return a InvalidPreferencePayload Error when status is BadRequest (400)" in {

      val actualResult = UpdateContactPreferenceHttpReads.read("","", HttpResponse(Status.BAD_REQUEST))
      val expectedResult = Left(InvalidPreferencePayload)

      actualResult shouldBe expectedResult
    }


    "return a DependentSystemUnavailable Error when status is SERVICE_UNAVAILABLE (503)" in {

      val actualResult = UpdateContactPreferenceHttpReads.read("","", HttpResponse(Status.SERVICE_UNAVAILABLE))
      val expectedResult = Left(DependentSystemUnavailable)

      actualResult shouldBe expectedResult
    }

    "return a UnexpectedError when status is 500)" in {

      val actualResult = UpdateContactPreferenceHttpReads.read("","", HttpResponse(Status.INTERNAL_SERVER_ERROR,Some(JsString("Error"))))
      val expectedResult = Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, """"Error""""))

      actualResult shouldBe expectedResult
    }
  }


  "JourneyHttpParser.ErrorResponse" when {

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
