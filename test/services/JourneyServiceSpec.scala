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

package services

import assets.JourneyTestConstants._
import connectors.httpParsers.JourneyHttpParser.InvalidJson
import connectors.mocks.MockJourneyConnector

class JourneyServiceSpec extends MockJourneyConnector {

  object TestJourneyService extends JourneyService(connector)

  "startSetJourney" when {

    "successful" should {

      "return a Journey model" in {

        mockStartSetJourney("testID")(Right(journeyModelMax))
        val actualResult = await(TestJourneyService.startSetJourney("testID"))
        val expectedResult = Right(journeyModelMax)

        actualResult shouldBe expectedResult
      }
    }

    "unsuccessful" should {

      "return an Error model" in {

        mockStartSetJourney("testID")(Left(InvalidJson))
        val actualResult = await(TestJourneyService.startSetJourney("testID"))
        val expectedResult = Left(InvalidJson)

        actualResult shouldBe expectedResult
      }
    }
  }

  "startUpdateJourney" when {

    "successful" should {

      "return a Journey model" in {

        mockStartUpdateJourney("testID")(Right(journeyModelMax))
        val actualResult = await(TestJourneyService.startUpdateJourney("testID"))
        val expectedResult = Right(journeyModelMax)

        actualResult shouldBe expectedResult
      }
    }

    "unsuccessful" should {

      "return an Error model" in {

        mockStartUpdateJourney("testID")(Left(InvalidJson))
        val actualResult = await(TestJourneyService.startUpdateJourney("testID"))
        val expectedResult = Left(InvalidJson)

        actualResult shouldBe expectedResult
      }
    }
  }
}
