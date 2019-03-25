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

package models

import assets.JourneyTestConstants._
import assets.AddressTestConstants._
import play.api.libs.json.Json
import utils.TestUtils

class JourneySpec extends TestUtils {

  "Journey" should {

    "have a regime" in {

      val actualResult = journeyModelMax.regime
      val expectedResult = regimeModel

      actualResult shouldBe expectedResult
    }

    "have a continueUrl" in {

      val actualResult = journeyModelMax.continueUrl
      val expectedResult = continueUrl

      actualResult shouldBe expectedResult
    }

    "have an email" in {

      val actualResult = journeyModelMax.email
      val expectedResult = email

      actualResult shouldBe expectedResult
    }

    "have a service name" in {

      val actualResult = journeyModelMax.serviceName
      val expectedResult = Some(serviceName)

      actualResult shouldBe expectedResult
    }

    "have an address" in {

      val actualResult = journeyModelMax.address
      val expectedResult = addressModelMax

      actualResult shouldBe expectedResult
    }


    "read regime from Json to the correct Journey" in {

      val actualResult: Journey = journeyJsonMax.as[Journey]
      val expectedResult: Journey = journeyModelMax

      actualResult shouldBe expectedResult
    }

    "write to the correct Json" in {

      val actualResult = Json.toJson(journeyModelMax)
      val expectedResult = journeyJsonMax

      actualResult shouldBe expectedResult
    }
  }
}
