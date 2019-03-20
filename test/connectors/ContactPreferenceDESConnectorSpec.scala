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

package connectors

import assets.ContactPreferencesTestConstants._
import assets.BaseTestConstants._
import assets.JourneyTestConstants._
import connectors.httpParsers.GetDesContactPreferenceHttpParser.{NotFound, UnexpectedFailure}
import models.{Digital, MTDVAT, RegimeType, VRN}
import play.mvc.Http.Status
import utils.{MockHttpClient, TestUtils}

class ContactPreferenceDESConnectorSpec extends TestUtils with MockHttpClient {

  object TestContactPreferenceDesConnector extends ContactPreferenceDESConnector(
    mockHttpClient,
    appConfig
  )

  "ContactPreferenceDesConnector" when {

    "getContactPreference is successful" should {

      "return a ContactPreference model" in {

        mockHttpGet(Right(digitalPreferenceModel))

        val expectedResult = Right(digitalPreferenceModel)
        val actualResult = await(TestContactPreferenceDesConnector.getContactPreference(regimeModel))

        actualResult shouldBe expectedResult
      }
    }

    "getContactPreferenceDes is unsuccessful" should {

      "return a NotFound ErrorResponse" when {

        "the response status is NotFound" in {

          mockHttpGet(Left(NotFound))

          val expectedResult = Left(NotFound)
          val actualResult = await(TestContactPreferenceDesConnector.getContactPreference(regimeModel))

          actualResult shouldBe expectedResult
        }
      }

      "return a UnexpectedError" when {

        "there is a failed future and exception thrown" in {

          mockHttpGetFailed()

          val expectedResult = Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, "Unexpected Error: I Died"))
          val actualResult = await(TestContactPreferenceDesConnector.getContactPreference(regimeModel))

          actualResult shouldBe expectedResult

        }
      }
    }

    "an id is given to ContactPreferenceDesUrl" should {

      "have the correct url" in {

        val expectedResult = s"http://localhost:9592/contact-preferences/${MTDVAT.id}/${VRN.value}/$testVatNumber"
        val actualResult = TestContactPreferenceDesConnector.contactPreferenceDesUrl(regimeModel)

        actualResult shouldBe expectedResult
      }
    }
  }
}
