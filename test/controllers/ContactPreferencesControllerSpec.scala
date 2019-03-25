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

import assets.ContactPreferencesTestConstants.digitalPreferenceModel
import assets.JourneyTestConstants.{journeyId, journeyModelMax, _}
import assets.messages.ContactPreferencesMessages.{title => pageTitle}
import audit.mocks.MockAuditConnector
import audit.models.ContactPreferenceAuditModel
import connectors.httpParsers.GetContactPreferenceHttpParser
import connectors.httpParsers.JourneyHttpParser.NotFound
import connectors.httpParsers.StoreContactPreferenceHttpParser.{InvalidPreferencePayload, Success}
import controllers.mocks.MockAuthService
import forms.{ContactPreferencesForm, YesNoMapping}
import models.{Digital, Paper}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.mocks.{MockContactPreferencesService, MockJourneyService}
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import utils.ControllerTestUtils

import scala.concurrent.Future

class ContactPreferencesControllerSpec extends ControllerTestUtils with MockContactPreferencesService
  with MockJourneyService with MockAuthService with MockAuditConnector {

  object TestContactPreferencesController extends ContactPreferencesController(
    messagesApi, mockAuthService, mockJourneyService, mockContactPreferencesService, errorHandler, mockAuditConnector, appConfig
  )

  "ContactPreferencesController.show" when {

    def result: Future[Result] = TestContactPreferencesController.setRouteShow(journeyId)(fakeRequest)

    "a journey can be retrieved from the backend" when {

      "the user is authorised" should {

        "return an OK (200)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthenticated(EmptyPredicate)

          status(result) shouldBe Status.OK
        }
      }

      "the user is NOT authorised" should {

        "return an FORBIDDEN (403)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthorise(EmptyPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

          status(result) shouldBe Status.FORBIDDEN
        }
      }

    }

    "a journey can NOT be retrieved from the backend" when {

      "return an NOT_FOUND (404)" in {
        mockJourney(journeyId)(Left(NotFound))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

    }

  }

  "ContactPreferencesController.showAuthenticated" when {

    "a journey can be retrieved from the backend" when {

      "the user is authorised" should {

        "the user has a pre selected preference" should {

          lazy val result: Future[Result] = TestContactPreferencesController.updateRouteShow(journeyId)(fakeRequest)

          "return an OK (200)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(EmptyPredicate)
            mockGetContactPreference(regimeModel)(Right(digitalPreferenceModel))
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "display the correct page with the correct option selected" in {
            title(result) shouldBe pageTitle
            selectElement(result, "#yes").hasAttr("checked") shouldBe true
            selectElement(result, "#no").hasAttr("checked") shouldBe false
          }
        }

        "the user does NOT have a pre selected preference" should {

          lazy val result: Future[Result] = TestContactPreferencesController.updateRouteShow(journeyId)(fakeRequest)

          "return an OK (200)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(EmptyPredicate)
            mockGetContactPreference(regimeModel)(Left(GetContactPreferenceHttpParser.NotFound))
            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "display the correct page with no option selected" in {
            title(result) shouldBe pageTitle
            //TODO make this actually check if the option has actually been selected
            selectElement(result, "#yes").hasAttr("checked") shouldBe false
            selectElement(result, "#no").hasAttr("checked") shouldBe false
          }
        }
      }

      "the user is NOT authorised" should {

        lazy val result: Future[Result] = TestContactPreferencesController.updateRouteShow(journeyId)(fakeRequest)

        "return an FORBIDDEN (403)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthorise(EmptyPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

          status(result) shouldBe Status.FORBIDDEN
        }
      }
    }

    "a journey can NOT be retrieved from the backend" when {

      lazy val result: Future[Result] = TestContactPreferencesController.updateRouteShow(journeyId)(fakeRequest)

      "return an NOT_FOUND (404)" in {
        mockJourney(journeyId)(Left(NotFound))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "ContactPreferencesController.submit" when {

    "a journey can be retrieved from the backend" when {

      "the user is authorised" when {

        "'Yes' option is entered" when {

          "A success response is returned from the PreferenceService" should {

            lazy val result = TestContactPreferencesController.setRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
              ContactPreferencesForm.yesNo -> YesNoMapping.option_yes
            ))

            "return an SEE_OTHER (303) status" in {

              mockJourney(journeyId)(Right(journeyModelMax))
              mockAuthenticated(EmptyPredicate)
              mockStoreJourneyPreference(journeyId, Digital)(Right(Success))

              verifyExplicitAudit(
                ContactPreferenceAuditModel.auditType,
                ContactPreferenceAuditModel(
                  journeyModelMax.regime,
                  None,
                  journeyModelMax.email,
                  Digital
                )
              )

              status(result) shouldBe Status.SEE_OTHER
            }

            "redirect to the continueUrl posted as part of the JourneyModel" in {
              redirectLocation(result) shouldBe Some(s"${journeyModelMax.continueUrl}?preferenceId=$journeyId")
            }
          }

          "An error response is returned from the PreferenceService" should {

            lazy val result = TestContactPreferencesController.setRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
              ContactPreferencesForm.yesNo -> YesNoMapping.option_yes
            ))

            "return the error status" in {

              mockJourney(journeyId)(Right(journeyModelMax))
              mockAuthenticated(EmptyPredicate)
              mockStoreJourneyPreference(journeyId, Digital)(Left(InvalidPreferencePayload))

              verifyExplicitAudit(
                ContactPreferenceAuditModel.auditType,
                ContactPreferenceAuditModel(
                  journeyModelMax.regime,
                  None,
                  journeyModelMax.email,
                  Digital
                )
              )

              status(result) shouldBe Status.INTERNAL_SERVER_ERROR
            }
          }
        }

        "'No' option is entered" when {

          "A success response is returned from the PreferenceService" should {

            lazy val result = TestContactPreferencesController.setRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
              ContactPreferencesForm.yesNo -> YesNoMapping.option_no
            ))

            "return an SEE_OTHER (303) status" in {

              mockJourney(journeyId)(Right(journeyModelMax))
              mockAuthenticated(EmptyPredicate)
              mockStoreJourneyPreference(journeyId, Paper)(Right(Success))

              verifyExplicitAudit(
                ContactPreferenceAuditModel.auditType,
                ContactPreferenceAuditModel(
                  journeyModelMax.regime,
                  None,
                  journeyModelMax.email,
                  Paper
                )
              )

              status(result) shouldBe Status.SEE_OTHER
            }

            "redirect to the continueUrl posted as part of the JourneyModel" in {
              redirectLocation(result) shouldBe Some(s"${journeyModelMax.continueUrl}?preferenceId=$journeyId")
            }
          }

          "An error response is returned from the PreferenceService" should {

            lazy val result = TestContactPreferencesController.setRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
              ContactPreferencesForm.yesNo -> YesNoMapping.option_no
            ))

            "return the error status" in {

              mockJourney(journeyId)(Right(journeyModelMax))
              mockAuthenticated(EmptyPredicate)
              mockStoreJourneyPreference(journeyId, Paper)(Left(InvalidPreferencePayload))

              verifyExplicitAudit(
                ContactPreferenceAuditModel.auditType,
                ContactPreferenceAuditModel(
                  journeyModelMax.regime,
                  None,
                  journeyModelMax.email,
                  Paper
                )
              )

              status(result) shouldBe Status.INTERNAL_SERVER_ERROR
            }
          }
        }

        "no radio option is selected" should {

          "return a BAD_REQUEST (400)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(EmptyPredicate)

            val result = TestContactPreferencesController.setRouteSubmit(journeyId)(FakeRequest("POST", "/"))

            status(result) shouldBe Status.BAD_REQUEST
          }
        }
      }

      "the user is NOT authorised" should {

        "return an FORBIDDEN (403)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthorise(EmptyPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

          val result = TestContactPreferencesController.setRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
            ContactPreferencesForm.yesNo -> YesNoMapping.option_yes
          ))

          status(result) shouldBe Status.FORBIDDEN
        }
      }
    }

    "a journey can NOT be retrieved from the backend" when {

      "return an NOT_FOUND (404)" in {
        mockJourney(journeyId)(Left(NotFound))

        val result = TestContactPreferencesController.setRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
          ContactPreferencesForm.yesNo -> YesNoMapping.option_yes
        ))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
