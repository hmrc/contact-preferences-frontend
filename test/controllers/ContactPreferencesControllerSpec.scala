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

package controllers

import forms.{ContactPreferencesForm, YesNoMapping}
import play.api.http.Status
import play.api.test.FakeRequest
import utils.TestUtils

class ContactPreferencesControllerSpec extends TestUtils {

  object TestContactPreferencesController extends ContactPreferencesController(messagesApi, mockAuthConnector, appConfig)

  "ContactPreferencesController.show" should {

    lazy val result = TestContactPreferencesController.show(fakeRequest)

    "return an OK (200)" in {
      status(result) shouldBe Status.OK
    }
  }

  "ContactPreferencesController.submit" when {

    "'Yes' option is entered" should {

      lazy val result = TestContactPreferencesController.submit(FakeRequest("POST", "/").withFormUrlEncodedBody(
        ContactPreferencesForm.yesNo -> YesNoMapping.option_yes
      ))

      "return an SEE_OTHER (303)" in {
        status(result) shouldBe Status.SEE_OTHER
      }
    }

    "'No' option is entered" should {

      lazy val result = TestContactPreferencesController.submit(FakeRequest("POST", "/").withFormUrlEncodedBody(
        ContactPreferencesForm.yesNo -> YesNoMapping.option_no
      ))

      "return an SEE_OTHER (303)" in {
        status(result) shouldBe Status.SEE_OTHER
      }
    }

    "no option is entered" should {

      lazy val result = TestContactPreferencesController.submit(FakeRequest("POST", "/"))

      "return a BAD_REQUEST (400)" in {
        status(result) shouldBe Status.BAD_REQUEST
      }
    }
  }

}
