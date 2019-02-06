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

class RegimeSetup {

  val regimeType = RegimeType(MTDVAT.id)
  val identifier = Identifier(VRN.value)
  val id = Id(identifier, "999999999")
  val regime = Regime(regimeType, id)

  val incomingRegimeJson: JsObject = Json.obj(
    "regimeType" -> "VAT",
    "identifier" -> Json.obj(
      "key" -> VRN.value,
              "value" -> "999999999"
    )
  )
}

class RegimeSpec extends TestUtils {

  "Regime" should {
    "contain a regime type" in new RegimeSetup {

      val actualResult = regime.regimeType
      val expectedResult = regimeType

      actualResult shouldBe expectedResult
    }

    "contain an identifier" in new RegimeSetup {

      val actualResult = regime.identifier
      val expectedResult = id

      actualResult shouldBe expectedResult
    }

    "read incoming json to a model" in new RegimeSetup {

      val actualResult = incomingRegimeJson.as[Regime]
      val expectedResult = regime

      actualResult shouldBe expectedResult
    }

    "write outgoing data to json" in new RegimeSetup {

      val actualResult = Json.toJson(regime)
      val expectedResult = incomingRegimeJson

      actualResult shouldBe expectedResult
    }
  }
}
