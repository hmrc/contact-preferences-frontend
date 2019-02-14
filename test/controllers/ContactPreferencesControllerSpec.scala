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

import assets.JourneyTestConstants.{journeyId, journeyModelMax}
import controllers.mocks.MockAuthService
import forms.{ContactPreferencesForm, YesNoMapping}
import play.api.http.Status
import play.api.test.FakeRequest
import services.mocks.MockJourneyService
import utils.TestUtils

class ContactPreferencesControllerSpec extends TestUtils with MockJourneyService with MockAuthService {

  object TestContactPreferencesController extends ContactPreferencesController(messagesApi, mockAuthService, mockJourneyService, appConfig)

  "ContactPreferencesController.show" should {

    "return an OK (200)" in {
      mockJourney(journeyId)(Right(journeyModelMax))
      mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)

      val result = TestContactPreferencesController.show(journeyId)(fakeRequest)

      status(result) shouldBe Status.OK
    }
  }

  "ContactPreferencesController.submit" when {

    "'Yes' option is entered" should {

      "return an SEE_OTHER (303)" in {
        mockJourney(journeyId)(Right(journeyModelMax))
        mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)

        val result = TestContactPreferencesController.submit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
          ContactPreferencesForm.yesNo -> YesNoMapping.option_yes
        ))

        //TODO
        status(result) shouldBe Status.NOT_IMPLEMENTED
      }
    }

    "'No' option is entered" should {

      "return an SEE_OTHER (303)" in {
        mockJourney(journeyId)(Right(journeyModelMax))
        mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)

        val result = TestContactPreferencesController.submit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
          ContactPreferencesForm.yesNo -> YesNoMapping.option_no
        ))

        //TODO
        status(result) shouldBe Status.NOT_IMPLEMENTED
      }
    }

    "no option is entered" should {

      "return a BAD_REQUEST (400)" in {
        mockJourney(journeyId)(Right(journeyModelMax))
        mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)

        val result = TestContactPreferencesController.submit(journeyId)(FakeRequest("POST", "/"))

        status(result) shouldBe Status.BAD_REQUEST
      }
    }
  }
}
