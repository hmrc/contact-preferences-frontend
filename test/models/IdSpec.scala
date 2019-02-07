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

class IdSetup {

  val idModel: Id = Id(VRN, "999999999")
  val idJson: JsObject = Json.obj(
    "key" -> VRN.value,
    "value" -> "999999999"
  )
}

class IdSpec extends TestUtils {

  "Id" should {

    "contain an Identifier for the key" in new IdSetup {

      val actualResult: Identifier = idModel.key
      val expectedResult: VRN.type = VRN

      actualResult shouldBe expectedResult
    }

    "contain an value" in new IdSetup {

      val actualResult: String = idModel.value
      val expectedResult: String = "999999999"

      actualResult shouldBe expectedResult
    }

    "read from Json to the correct model" in new IdSetup {

      val actualResult: Id = idJson.as[Id]
      val expectedResult: Id = idModel

      actualResult shouldBe expectedResult
    }

    "write to Json correctly" in new IdSetup {

      val actualResult: JsValue = Json.toJson(idModel)
      val expectedResult: JsValue = idJson

      actualResult shouldBe expectedResult
    }
  }
}
