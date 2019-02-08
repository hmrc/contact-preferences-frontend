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

import utils.{MockHttpClient, TestUtils}
import assets.JourneyTestConstants._
import models.{ErrorModel, Journey}
import play.api.http.Status

class JourneyConnectorSpec extends TestUtils with MockHttpClient {

  def setup(response: Either[ErrorModel, Journey]): JourneyConnector = {
    mockHttpGet[Either[ErrorModel, Journey]](response)
    new JourneyConnector(mockHttpClient, appConfig)
  }

  "JourneyConnector" when {

    "getJourney is successful" should {

      lazy val connector = setup(Right(journeyModelMax))

      "return a Journey model" in {

        val actualResult = await(connector.getJourney("id"))
        val expectedResult = Right(journeyModelMax)

        actualResult shouldBe expectedResult
      }
    }

    "getJourney is unsuccessful" should {

      val errorModel = ErrorModel(Status.INTERNAL_SERVER_ERROR, "Error Model")
      lazy val connector = setup(Left(errorModel))

      "return an ErrorModel" in {

        val actualResult = await(connector.getJourney("id"))
        val expectedResult = Left(errorModel)

        actualResult shouldBe expectedResult
      }
    }

    "an id is given to journeyUrl" should {

      object TestJourneyConnector extends JourneyConnector(
        mockHttpClient,
        appConfig
      )

      "have the correct url" in {

        val actualResult = TestJourneyConnector.journeyUrl("anId")
        val expectedResult = "http://localhost:9592/contact-preferences/journey/anId"

        actualResult shouldBe expectedResult
      }
    }
  }
}
