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

package forms

import forms.ContactPreferencesForm._
import forms.PreferenceMapping._
import models.{Email, Letter}
import play.api.data.FormError
import uk.gov.hmrc.play.test.UnitSpec

class ContactPreferencesFormSpec extends UnitSpec {

  val error = "error.contact_preferences.summary"

  "YesNoForm" should {
    "successfully parse a Email" in {
      val res = contactPreferencesForm.bind(Map(preference -> option_email))
      res.value should contain(Email)
    }

    "successfully parse a Digital" in {
      val res = contactPreferencesForm.bind(Map(preference -> option_letter))
      res.value should contain(Letter)
    }

    "fail when nothing has been entered" in {
      val res = contactPreferencesForm.bind(Map.empty[String, String])
      res.errors should contain(FormError(preference, error))
    }
  }
}
