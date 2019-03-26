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
import play.api.libs.json.{JsValue, Json}
import _root_.utils.TestUtils

class IdSpec extends TestUtils {

  "Id" should {

    "contain an Identifier for the key" in {

      val actualResult: Identifier = idModel.key
      val expectedResult: Identifier = VRN

      actualResult shouldBe expectedResult
    }

    "contain an value" in {

      val actualResult: String = idModel.value
      val expectedResult: String = "999999999"

      actualResult shouldBe expectedResult
    }

    "read from Json to the correct model" in {

      val actualResult: Id = idJson.as[Id]
      val expectedResult: Id = idModel

      actualResult shouldBe expectedResult
    }

    "write to Json correctly" in {

      val actualResult: JsValue = Json.toJson(idModel)
      val expectedResult: JsValue = idJson

      actualResult shouldBe expectedResult
    }
  }
}
