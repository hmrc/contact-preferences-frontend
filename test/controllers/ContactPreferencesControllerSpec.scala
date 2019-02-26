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
import connectors.httpParsers.JourneyHttpParser.NotFound
import connectors.httpParsers.StorePreferenceHttpParser.{InvalidPreferencePayload, Success}
import controllers.mocks.MockAuthService
import forms.{ContactPreferencesForm, YesNoMapping}
import models.{Digital, Paper}
import play.api.http.{HeaderNames, Status}
import play.api.mvc.Result
import play.api.test.FakeRequest
import services.mocks.{MockJourneyService, MockPreferenceService}
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import utils.TestUtils

import scala.concurrent.Future

class ContactPreferencesControllerSpec extends TestUtils with MockPreferenceService with MockJourneyService with MockAuthService {

  object TestContactPreferencesController extends ContactPreferencesController(
    messagesApi, mockAuthService, mockJourneyService, mockPreferenceService, appConfig
  )

  "ContactPreferencesController.show" when {

    def result: Future[Result] = TestContactPreferencesController.show(journeyId)(fakeRequest)

    "a journey can be retrieved from the backend" when {

      "the user is authorised" should {

        "return an OK (200)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)

          status(result) shouldBe Status.OK
        }
      }

      "the user is NOT authorised" should {

        "return an FORBIDDEN (403)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

          status(result) shouldBe Status.FORBIDDEN
        }
      }

    }

    "a journey can NOT be retrieved from the backend" when {

      "return an NOT_FOUND (404)" in {
        mockJourney(journeyId)(Left(NotFound))

        status(result) shouldBe Status.NOT_FOUND
      }

    }

  }

  "ContactPreferencesController.submit" when {

    "a journey can be retrieved from the backend" when {

      "the user is authorised" when {

        "'Yes' option is entered" when {

          "A success response is returned from the PreferenceService" should {

            lazy val result = TestContactPreferencesController.submit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
              ContactPreferencesForm.yesNo -> YesNoMapping.option_yes
            ))

            "return an SEE_OTHER (303) status" in {

              mockJourney(journeyId)(Right(journeyModelMax))
              mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
              mockStoreJourneyPreference(journeyId, Digital)(Right(Success))

              status(result) shouldBe Status.SEE_OTHER
            }

            "redirect to the continueUrl posted as part of the JourneyModel" in {
              redirectLocation(result) shouldBe Some(journeyModelMax.continueUrl)
            }
          }

          "An error response is returned from the PreferenceService" should {

            lazy val result = TestContactPreferencesController.submit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
              ContactPreferencesForm.yesNo -> YesNoMapping.option_yes
            ))

            "return the error status" in {

              mockJourney(journeyId)(Right(journeyModelMax))
              mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
              mockStoreJourneyPreference(journeyId, Digital)(Left(InvalidPreferencePayload))

              status(result) shouldBe InvalidPreferencePayload.status
            }

            "Return the appropriate error message" in {
              await(bodyOf(result)) shouldBe InvalidPreferencePayload.body
            }
          }
        }

        "'No' option is entered" when {

          "A success response is returned from the PreferenceService" should {

            lazy val result = TestContactPreferencesController.submit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
              ContactPreferencesForm.yesNo -> YesNoMapping.option_no
            ))

            "return an SEE_OTHER (303) status" in {

              mockJourney(journeyId)(Right(journeyModelMax))
              mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
              mockStoreJourneyPreference(journeyId, Paper)(Right(Success))

              status(result) shouldBe Status.SEE_OTHER
            }

            "redirect to the continueUrl posted as part of the JourneyModel" in {
              redirectLocation(result) shouldBe Some(journeyModelMax.continueUrl)
            }
          }

          "An error response is returned from the PreferenceService" should {

            lazy val result = TestContactPreferencesController.submit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
              ContactPreferencesForm.yesNo -> YesNoMapping.option_no
            ))

            "return the error status" in {

              mockJourney(journeyId)(Right(journeyModelMax))
              mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)
              mockStoreJourneyPreference(journeyId, Paper)(Left(InvalidPreferencePayload))

              status(result) shouldBe InvalidPreferencePayload.status
            }

            "Return the appropriate error message" in {
              await(bodyOf(result)) shouldBe InvalidPreferencePayload.body
            }
          }
        }

        "no radio option is selected" should {

          "return a BAD_REQUEST (400)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthRetrieveMtdVatEnrolled(vatAuthPredicate)

            val result = TestContactPreferencesController.submit(journeyId)(FakeRequest("POST", "/"))

            status(result) shouldBe Status.BAD_REQUEST
          }
        }
      }

      "the user is NOT authorised" should {

        "return an FORBIDDEN (403)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthorise(vatAuthPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

          val result = TestContactPreferencesController.submit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
            ContactPreferencesForm.yesNo -> YesNoMapping.option_yes
          ))

          status(result) shouldBe Status.FORBIDDEN
        }
      }
    }

    "a journey can NOT be retrieved from the backend" when {

      "return an NOT_FOUND (404)" in {
        mockJourney(journeyId)(Left(NotFound))

        val result = TestContactPreferencesController.submit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
          ContactPreferencesForm.yesNo -> YesNoMapping.option_yes
        ))

        status(result) shouldBe Status.NOT_FOUND
      }
    }
  }
}
