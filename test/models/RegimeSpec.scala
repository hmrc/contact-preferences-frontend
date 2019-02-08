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

import play.api.libs.json.{JsObject, Json}
import utils.TestUtils
import assets.JourneyTestConstants._

class RegimeSpec extends TestUtils {

  "Regime" should {
    "contain a regime type" in {

      val actualResult = regimeModel.regimeType
      val expectedResult = MTDVAT

      actualResult shouldBe expectedResult
    }

    "contain an identifier" in {

      val actualResult = regimeModel.identifier
      val expectedResult = idModel

      actualResult shouldBe expectedResult
    }

    "read incoming json to a model" in {

      val actualResult = regimeJson.as[Regime]
      val expectedResult = regimeModel

      actualResult shouldBe expectedResult
    }

    "write outgoing data to json" in {

      val actualResult = Json.toJson(regimeModel)
      val expectedResult = regimeJson

      actualResult shouldBe expectedResult
    }
  }
}
