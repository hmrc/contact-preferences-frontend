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

import play.api.data.validation.ValidationError
import play.api.libs.json._
import _root_.utils.TestUtils
import assets.JourneyTestConstants._

class IdentifierSpec extends TestUtils {

  "Identifier" should {
    "contain a key" in {

      val actualResult = VRN.value
      val expectedResult = "vrn"

      actualResult shouldBe expectedResult
    }

    "yield a VRN object when passed VRN string" in {

      val actualResult = Identifier.apply("vrn")
      val expectedResult = VRN

      actualResult shouldBe expectedResult
    }

    "throw an JsResultException when passed an invalid identifier" in {

      val actualResult = intercept[JsResultException] {
        Identifier.apply("INVALID_IDENTIFIER")
      }
      val expectedResult = JsResultException(Seq(__ -> Seq(ValidationError(Seq(s"Invalid Identifier: INVALID_IDENTIFIER, valid identifiers: vrn")))))
      actualResult shouldBe expectedResult
    }

    "deconstruct it's value when incorrectly pattern matched" in {

      val actualResult = Identifier.unapply(VRN)
      val expectedResult = "vrn"

      actualResult shouldBe expectedResult
    }

    "read an incoming js string and construct into a scala model" in {

      val actualResult = identifierJson.as[Identifier]
      val expectedResult = VRN

      actualResult shouldBe expectedResult
    }

    "write an outgoing model to a js string" in {

      val actualResult = Json.toJson(VRN)
      val expectedResult = identifierJson

      actualResult shouldBe expectedResult
    }
  }
}
