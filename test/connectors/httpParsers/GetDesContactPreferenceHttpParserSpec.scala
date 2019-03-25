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

import connectors.httpParsers.GetContactPreferenceHttpParser._
import models.{ContactPreferenceModel, Email}
import play.api.http.Status
import uk.gov.hmrc.http.HttpResponse
import utils.TestUtils
import assets.ContactPreferencesTestConstants._
import play.api.libs.json.JsString


class GetDesContactPreferenceHttpParserSpec extends TestUtils {

  "GetDesContactPreferenceHttpParser.GetDesContactPreferenceReads" should {

    "return a ContactPreferenceModel when status is OK (200)" in {

      val actualResult = GetDesContactPreferenceHttpReads.read("", "", HttpResponse(
        responseStatus = Status.OK,
        responseJson = Some(digitalPreferenceJson)
      ))
      val expectedResult = Right(ContactPreferenceModel(Email))

      actualResult shouldBe expectedResult
    }

    "return a InvalidJson when status returned is OK(200) but Json is invalid" in {

      val actualResult = GetDesContactPreferenceHttpReads.read("", "", HttpResponse(
        responseStatus = Status.OK,
        responseJson = Some(JsString(""))
      ))
      val expectedResult = Left(InvalidJson)

      actualResult shouldBe expectedResult
    }

    "return a NotFound when status NOT_FOUND(404) is returned" in {

      val actualResult = GetDesContactPreferenceHttpReads.read("", "", HttpResponse(responseStatus = Status.NOT_FOUND))
      val expectedResult = Left(NotFound)

      actualResult shouldBe expectedResult
    }

    "return a DependentSystemUnavailable when status SERVICE_UNAVAILABLE(503) is returned" in {

      val actualResult = GetDesContactPreferenceHttpReads.read("", "", HttpResponse(responseStatus = Status.SERVICE_UNAVAILABLE))
      val expectedResult = Left(DependentSystemUnavailable)

      actualResult shouldBe expectedResult
    }

    "return an UnexpectedFailure when status is INTERNAL_SERVER_ERROR (500)" in {

      val actualResult = GetDesContactPreferenceHttpReads.read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR))
      val expectedResult = Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, "Error"))

      actualResult shouldBe expectedResult
    }
  }
}
