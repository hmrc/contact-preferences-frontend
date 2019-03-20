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

import assets.JourneyTestConstants._
import connectors.httpParsers.JourneyHttpParser.{NotFound, UnexpectedFailure}
import play.mvc.Http.Status
import utils.{MockHttpClient, TestUtils}

class JourneyConnectorSpec extends TestUtils with MockHttpClient {

  object TestContactPreferenceDesConnector extends ContactPreferencesDesConnector(
    mockHttpClient,
    appConfig
  )

  "JourneyConnector" when {

    "getJourney is successful" should {

      "return a Journey model" in {

        mockHttpGet(Right(journeyModelMax))

        val actualResult = await(TestJourneyConnector.getJourney("id"))
        val expectedResult = Right(journeyModelMax)

        actualResult shouldBe expectedResult
      }
    }

    "getJourney is unsuccessful" should {

      "return a NotFound ErrorResponse" when {

        "the response status is NotFound" in {

          mockHttpGet(Left(NotFound))

          val actualResult = await(TestJourneyConnector.getJourney("id"))
          val expectedResult = Left(NotFound)

          actualResult shouldBe expectedResult
        }
      }

      "return a UnexpectedError" when {

        "there is a failed future and exception thrown" in {

          mockHttpGetFailed()

          val actualResult = await(TestJourneyConnector.getJourney("id"))
          val expectedResult = Left(UnexpectedFailure(Status.INTERNAL_SERVER_ERROR, "Unexpected Error: I Died"))

          actualResult shouldBe expectedResult

        }

      }
    }

    "an id is given to journeyUrl" should {

      "have the correct url" in {

        val actualResult = TestJourneyConnector.journeyUrl("anId")
        val expectedResult = "http://localhost:9592/contact-preferences/journey/anId"

        actualResult shouldBe expectedResult
      }
    }
  }
}
