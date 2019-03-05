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
import assets.JourneyTestConstants._


class RegimeTypeSpec extends TestUtils {

  "RegimeType" should {

    class RegimeTypeSetup extends RegimeType {

      override val id: String = "id"
      override val enrolmentId: String = "enrolmentId"
      override val delegatedAuthRule: String = "delegatedAuthRule"
      override val desId: String = "desId"
    }

    "have an id" in new RegimeTypeSetup {

      val actualResult: String = id
      val expectedResult: String = "id"

      actualResult shouldBe expectedResult
    }

    "have an enrolmentId" in new RegimeTypeSetup {

      val actualResult: String = enrolmentId
      val expectedResult: String = "enrolmentId"

      actualResult shouldBe expectedResult
    }

    "have a delegatedAuthRule" in new RegimeTypeSetup {

      val actualResult: String = delegatedAuthRule
      val expectedResult: String = "delegatedAuthRule"

      actualResult shouldBe expectedResult
    }

    "have a desId" in new RegimeTypeSetup {

      val actualResult: String = desId
      val expectedResult: String = "desId"

      actualResult shouldBe expectedResult
    }
  }

  "MTDVAT" should {

    "yield a specific RegimeType when applied" in {

      val actualResult: RegimeType = RegimeType.apply("vat")
      val expectedResult = MTDVAT

      actualResult shouldBe expectedResult
    }

    "throw a JsResultException when an invalid RegimeType is given" in {

      val actualResult = intercept[JsResultException] {
        RegimeType.apply("invalid")
      }
      val expectedResult = JsResultException(Seq(__ -> Seq(ValidationError(Seq(s"Invalid regime type: invalid, valid regime type: VAT")))))
      actualResult shouldBe expectedResult
    }

    "yield the correct RegimeType id when unnapplied" in {

      val actualResult = RegimeType.unapply(MTDVAT)
      val expectedResult = "vat"

      actualResult shouldBe expectedResult
    }

    "read correctly from Json to the correct RegimeType" in {

      val actualResult = regimeTypeJson.as[RegimeType]
      val expectedResult = MTDVAT

      actualResult shouldBe expectedResult
    }

    "write to Json correctly" in {

      val actualResult = Json.toJson(MTDVAT)
      val expectedResult = regimeTypeJson

      actualResult shouldBe expectedResult
    }
  }
}
