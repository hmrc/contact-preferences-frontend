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
import connectors.httpParsers.StorePreferenceHttpParser._
import play.mvc.Http.Status
import utils.{MockHttpClient, TestUtils}

class PreferenceConnectorSpec extends TestUtils with MockHttpClient {

  object TestPreferenceConnector extends PreferenceConnector(mockHttpClient, appConfig)

  "PreferenceConnector" when {

    "storeJourneyPreference() is successful" should {

      "return a Preference model" in {

        mockHttpPut(digitalPreferenceModel)(Right(Success))

        val actualResult = await(TestPreferenceConnector.storeJourneyPreference("id", digitalPreferenceModel.preference))
        val expectedResult = Right(Success)

        actualResult shouldBe expectedResult
      }
    }

    "storeJourneyPreference() is unsuccessful" should {

      "return a NotFound ErrorResponse" when {

        "the response status is NotFound" in {

          mockHttpPut(digitalPreferenceModel)(Left(NotFound))

          val actualResult = await(TestPreferenceConnector.storeJourneyPreference("id", digitalPreferenceModel.preference))
          val expectedResult = Left(NotFound)

          actualResult shouldBe expectedResult
        }
      }

      "return a UnexpectedError" when {

        "there is a failed future and exception thrown" in {

          mockHttpPutFailed()

          val actualResult = await(TestPreferenceConnector.storeJourneyPreference("id", digitalPreferenceModel.preference))
          val expectedResult = Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, "Unexpected Error: I Died"))

          actualResult shouldBe expectedResult

        }

      }
    }

    "an id is given to journeyUrl" should {

      "have the correct url" in {

        val actualResult = TestPreferenceConnector.preferenceUrl("anId")
        val expectedResult = "http://localhost:9592/contact-preferences/anId"

        actualResult shouldBe expectedResult
      }
    }
  }
}
