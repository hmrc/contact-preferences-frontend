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

import assets.BaseTestConstants.testVatNumber
import assets.ContactPreferencesTestConstants._
import assets.JourneyTestConstants._
import connectors.httpParsers.UpdateContactPreferenceHttpParser.Success
import connectors.httpParsers.{GetContactPreferenceHttpParser => GetHttpParser, StoreContactPreferenceHttpParser => StoreHttpParser, UpdateContactPreferenceHttpParser => UpdateHttpParser}
import models.{Email, MTDVAT, VRN}
import play.mvc.Http.Status
import utils.{MockHttpClient, TestUtils}

class ContactPreferencesConnectorSpec extends TestUtils with MockHttpClient {

  object TestContactPreferencesConnector extends ContactPreferencesConnector(mockHttpClient, appConfig)

  ".storePreferenceUrl" should {

    "have the correct url" in {

      val actualResult = TestContactPreferencesConnector.storePreferenceUrl("anId")
      val expectedResult = "http://localhost:9592/contact-preferences/anId"

      actualResult shouldBe expectedResult
    }
  }

  ".preferenceUrl" should {

    "have the correct url" in {

      val expectedResult = s"http://localhost:9592/contact-preferences/${MTDVAT.id}/${VRN.value}/$testVatNumber"
      val actualResult = TestContactPreferencesConnector.preferenceUrl(regimeModel)

      actualResult shouldBe expectedResult
    }
  }

  ".storeContactPreference()" when {

    "is successful" should {

      "return a Preference model" in {

        mockHttpPut(digitalPreferenceModel)(Right(StoreHttpParser.Success))

        val actualResult = await(TestContactPreferencesConnector.storeContactPreference("id", digitalPreferenceModel.preference))
        val expectedResult = Right(StoreHttpParser.Success)

        actualResult shouldBe expectedResult
      }
    }

    "is unsuccessful" should {

      "return a NotFound ErrorResponse" when {

        "the response status is NotFound" in {

          mockHttpPut(digitalPreferenceModel)(Left(StoreHttpParser.NotFound))

          val actualResult = await(TestContactPreferencesConnector.storeContactPreference("id", digitalPreferenceModel.preference))
          val expectedResult = Left(StoreHttpParser.NotFound)

          actualResult shouldBe expectedResult
        }
      }

      "return a UnexpectedError" when {

        "there is a failed future and exception thrown" in {

          mockHttpPutFailed()

          val actualResult = await(TestContactPreferencesConnector.storeContactPreference("id", digitalPreferenceModel.preference))
          val expectedResult = Left(StoreHttpParser.UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, "Unexpected Error: I Died"))

          actualResult shouldBe expectedResult

        }

      }
    }
  }

  ".getContactPreference()" when {

    "is successful" should {

      "return a ContactPreference model" in {

        mockHttpGet(Right(digitalPreferenceModel))

        val expectedResult = Right(digitalPreferenceModel)
        val actualResult = await(TestContactPreferencesConnector.getContactPreference(regimeModel))

        actualResult shouldBe expectedResult
      }
    }

    "is unsuccessful" should {

      "return a NotFound ErrorResponse" when {

        "the response status is NotFound" in {

          mockHttpGet(Left(GetHttpParser.NotFound))

          val expectedResult = Left(GetHttpParser.NotFound)
          val actualResult = await(TestContactPreferencesConnector.getContactPreference(regimeModel))

          actualResult shouldBe expectedResult
        }
      }

      "return a UnexpectedError" when {

        "there is a failed future and exception thrown" in {

          mockHttpGetFailed()

          val expectedResult = Left(GetHttpParser.UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, "Unexpected Error: I Died"))
          val actualResult = await(TestContactPreferencesConnector.getContactPreference(regimeModel))

          actualResult shouldBe expectedResult

        }
      }
    }
  }

  ".updateContactPreference()" when {

    "is successful" should {

      "return Success" in {

        mockHttpPut(Email)(Right(Success))

        val expectedResult = Right(Success)
        val actualResult = await(TestContactPreferencesConnector.updateContactPreference(regimeModel, Email))

        actualResult shouldBe expectedResult
      }
    }

    "is unsuccessful" should {

      "return a UnexpectedError" when {

        "there is a failed future and exception thrown" in {

          mockHttpPutFailed()

          val expectedResult = Left(UpdateHttpParser.UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, "Unexpected Error: I Died"))
          val actualResult = await(TestContactPreferencesConnector.updateContactPreference(regimeModel, Email))

          actualResult shouldBe expectedResult

        }
      }
    }
  }
}
