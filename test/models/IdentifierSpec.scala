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
import utils.TestUtils

class IdentifierSetup extends Identifier {

  override val value: String = "A-KEY"

  val jsString: JsString = JsString("VRN")
  val model: Identifier = Identifier(VRN.value)
}

class IdentifierSpec extends TestUtils {

  "Identifier" should {
    "contain a key" in new IdentifierSetup {

      val actualResult = value
      val expectedResult = "A-KEY"

      actualResult shouldBe expectedResult
    }

    "yield a VRN object when passed VRN string" in new IdentifierSetup {

      val actualResult = Identifier.apply("VRN")
      val expectedResult = VRN

      actualResult shouldBe expectedResult
    }

    "throw an JsResultException when passed an invalid identifier" in new IdentifierSetup {

      val actualResult = intercept[JsResultException] {
        Identifier.apply("INVALID_IDENTIFIER")
      }
      val expectedResult = JsResultException(Seq(__ -> Seq(ValidationError(Seq(s"Invalid Identifier: INVALID_IDENTIFIER, valid identifiers: VRN")))))
      actualResult shouldBe expectedResult
    }

    "deconstruct it's value when incorrectly pattern matched" in new IdentifierSetup {

      val actualResult = Identifier.unapply(VRN)
      val expectedResult = "VRN"

      actualResult shouldBe expectedResult
    }

    "read an incoming js string and construct into a scala model" in new IdentifierSetup {

      val actualResult = jsString.as[Identifier]
      val expectedResult = model

      actualResult shouldBe expectedResult
    }

    "write an outgoing model to a js string" in new IdentifierSetup {

      val actualResult = Json.toJson(model)
      val expectedResult = jsString

      actualResult shouldBe expectedResult
    }
  }
}
