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

import play.api.libs.json.{JsObject, JsValue, Json}
import utils.TestUtils

class JourneySetup {

  val regimeType = RegimeType(MTDVAT.id)
  val identifier = Identifier(VRN.value)
  val id = Id(identifier, "999999999")
  val regime = Regime(regimeType, id)
  val continueUrl: String =  "continue/url"
  val journeyModel = Journey(regime, continueUrl, Some("email@email.com"))

  val journeyJson: JsValue = Json.parse(
    """
      |{
      | "regime": {
      |   "regimeType": "VAT",
      |   "identifier": {
      |     "key": "VRN",
      |     "value": "999999999"
      |   }
      | },
      | "continueUrl": "continue/url",
      | "email": "email@email.com"
      |}
    """.stripMargin)
}

class JourneySpec extends TestUtils {

  "Journey" should {

    "have a regime" in new JourneySetup {

      val actualResult = journeyModel.regime
      val expectedResult = regime

      actualResult shouldBe expectedResult
    }

    "have a continueUrl" in new JourneySetup {

      val actualResult = journeyModel.continueUrl
      val expectedResult = "continue/url"

      actualResult shouldBe expectedResult
    }

    "have an email" in new JourneySetup {

      val actualResult = journeyModel.email
      val expectedResult = Some("email@email.com")

      actualResult shouldBe expectedResult
    }

    "read regime from Json to the correct Journey" in new JourneySetup {

      val actualResult: Regime = journeyJson.as[Journey].regime
      val expectedResult: Regime = regime

      actualResult shouldBe expectedResult
    }

    "read continueUrl from Json to the correct Journey" in new JourneySetup {

      val actualResult: String = journeyJson.as[Journey].continueUrl
      val expectedResult: String = continueUrl

      actualResult shouldBe expectedResult
    }

    /*"write to the correct Json" in new JourneySetup {

      val actualResult: JsValue = Json.toJson(journeyModel)
      val expectedResult: JsValue = journeyJson

      actualResult shouldBe expectedResult
    }*/
  }
}
