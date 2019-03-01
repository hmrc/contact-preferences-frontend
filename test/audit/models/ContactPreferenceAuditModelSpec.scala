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

import assets.BaseTestConstants.{testArn, testVatNumber, testEmail}
import models._
import play.api.libs.json.Json
import utils.TestUtils

class ContactPreferenceAuditModelSpec extends TestUtils {

  "ContactPreferenceAuditModel.writes" when {

    "given the max audit model" should {

      val input = ContactPreferenceAuditModel(
        RegimeModel(MTDVAT, Id(VRN, testVatNumber)),
        Some(testArn),
        Some(testEmail),
        Digital
      )

      val expectedOutput = Json.obj(
        "typeOfTax" -> MTDVAT.id,
        "identifierType" -> VRN.value,
        "identifierValue" -> testVatNumber,
        "agentReferenceNumber" -> testArn,
        "isAgent" -> true,
        "emailAddress" -> testEmail,
        "contactPreference" -> Digital.value
      )

      "return the correct JSON" in {
        Json.toJson(input) shouldBe expectedOutput
      }
    }

    "given the min audit model" should {

      val input = ContactPreferenceAuditModel(
        RegimeModel(MTDVAT, Id(VRN, testVatNumber)),
        None,
        None,
        Digital
      )

      val expectedOutput = Json.obj(
        "typeOfTax" -> MTDVAT.id,
        "identifierType" -> VRN.value,
        "identifierValue" -> testVatNumber,
        "isAgent" -> false,
        "contactPreference" -> Digital.value
      )

      "return the correct JSON" in {
        Json.toJson(input) shouldBe expectedOutput
      }
    }
  }
}
