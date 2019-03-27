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

package audit.models

import assets.BaseTestConstants._
import assets.AddressTestConstants._
import models._
import play.api.libs.json.Json
import utils.TestUtils

class SubmitContactPreferenceAuditModelSpec extends TestUtils {

  "ContactPreferenceAuditModel.writes" when {

    "given the max audit model" should {

      val input = SubmitContactPreferenceAuditModel(
        RegimeModel(MTDVAT, Id(VRN, testVatNumber)),
        Some(testArn),
        testEmail,
        addressModelMax,
        Email
      )

      val expectedOutput = Json.obj(
        "typeOfTax" -> MTDVAT.id,
        "identifierType" -> VRN.value,
        "identifierValue" -> testVatNumber,
        "agentReferenceNumber" -> testArn,
        "isAgent" -> true,
        "emailAddress" -> testEmail,
        "postalAddress" -> addressJsonMax,
        "contactPreference" -> Email.value
      )

      "return the correct JSON" in {
        Json.toJson(input) shouldBe expectedOutput
      }
    }

    "given the min audit model" should {

      val input = SubmitContactPreferenceAuditModel(
        RegimeModel(MTDVAT, Id(VRN, testVatNumber)),
        None,
        testEmail,
        addressModelMin,
        Email
      )

      val expectedOutput = Json.obj(
        "typeOfTax" -> MTDVAT.id,
        "identifierType" -> VRN.value,
        "identifierValue" -> testVatNumber,
        "isAgent" -> false,
        "emailAddress" -> testEmail,
        "postalAddress" -> addressJsonMin,
        "contactPreference" -> Email.value
      )

      "return the correct JSON" in {
        Json.toJson(input) shouldBe expectedOutput
      }
    }
  }
}
