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

import assets.AddressTestConstants._
import play.api.libs.json.Json
import utils.TestUtils

class AddressModelSpec extends TestUtils {

  "AddressModel.reads" when {

    "given maximum json values" should {

      "return the correct model" in {
        addressJsonMax.as[AddressModel] shouldEqual addressModelMax
      }
    }

    "given minimum json values" should {

      "return the correct model" in {
        addressJsonMin.as[AddressModel] shouldEqual addressModelMin
      }
    }

    "given incorrect json values" should {

      "throw an exception" in {
        Json.obj().validate[AddressModel].isError shouldBe true
      }
    }
  }

  "AddressModel.writes" when {

    "given maximum values in a model" should {

      "return the correct json" in {
         Json.toJson(addressModelMax) shouldBe addressJsonMax
      }
    }

    "given minimum values in a model" should {

      "return the correct json" in {
        Json.toJson(addressModelMin) shouldBe addressJsonMin
      }
    }
  }

}
