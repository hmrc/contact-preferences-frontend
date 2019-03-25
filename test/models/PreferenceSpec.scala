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

import play.api.libs.json.{JsResultException, JsString, Json, __}
import utils.JsonSugar
import _root_.utils.TestUtils

class PreferenceSpec extends TestUtils with JsonSugar {

  val digitalJson: JsString = JsString(Email.value)
  val paperJson: JsString = JsString(Letter.value)
  val invalidJson: JsString = JsString("foo")

  "Preference.apply" should {

    "when given a valid Preference" should {

      "for DIGITAL return Digital case object" in {
        Preference(Email.value) shouldBe Email
      }

      "for PAPER return Paper case object" in {
        Preference(Letter.value) shouldBe Letter
      }
    }

    "when given an invalid Preference" should {

      "for foo an InvalidPreference" in {
        intercept[JsResultException](Preference("foo")) shouldBe
          jsonError(__ \ "preference", s"Invalid Preference: FOO. Valid Preference set: (${Email.value}|${Letter.value})")
      }
    }
  }

  "Preference.unapply" should {

    "when given a valid Preference" should {

      "for Digital case object return DIGITAL " in {
        Preference.unapply(Email) shouldBe Email.value
      }

      "for Paper case object return PAPER" in {
        Preference.unapply(Letter) shouldBe Letter.value
      }
    }
  }

  "Preference.read" should {

    "when given a valid JSON document" should {

      "when given an valid Preference" should {

        "for DIGITAL return Digital case object" in {
          digitalJson.as[Preference] shouldBe Email
        }

        "for PAPER return Paper case object" in {
          paperJson.as[Preference] shouldBe Letter
        }
      }

      "when given an invalid Preference" should {

        "for invalidJson return InvalidPreference case object" in {
          intercept[JsResultException](invalidJson.as[Preference]) shouldBe
            jsonError(__ \ "preference", s"Invalid Preference: FOO. Valid Preference set: (${Email.value}|${Letter.value})")
        }
      }
    }

    "when given a invalid JSON document" should {

      "throw a JsResultException" in {
        val json = Json.obj("foo" -> "bar")
        json.validate[Preference].isError shouldBe true
      }
    }
  }

  "Preference.write" should {

    "when given an valid Preference" should {

      "for DIGITAL return Digital case object" in {
        Json.toJson(Email) shouldBe digitalJson
      }

      "for PAPER return Paper case object" in {
        Json.toJson(Letter) shouldBe paperJson
      }
    }
  }
}
