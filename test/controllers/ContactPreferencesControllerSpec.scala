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
import audit.models.{SubmitContactPreferenceAuditModel, ViewContactPreferenceAuditModel}
import config.SessionKeys
import connectors.httpParsers.GetContactPreferenceHttpParser
import connectors.httpParsers.JourneyHttpParser.{NotFound, Unauthorised}
import controllers.mocks.MockAuthService
import forms.{ContactPreferencesForm, PreferenceMapping}
import models.{Email, Letter}
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

  "ContactPreferencesController.setRouteShow" when {

    def result: Future[Result] = TestContactPreferencesController.setRouteShow(journeyId)(fakeRequest)

    "a journey can be retrieved from the backend" when {

      "the user is authorised" should {

        "return an OK (200)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthenticated(EmptyPredicate)

          verifyExplicitAudit(
            ViewContactPreferenceAuditModel.auditType,
            audit.models.ViewContactPreferenceAuditModel(
              journeyModelMax.regime,
              None,
              journeyModelMax.email,
              journeyModelMax.address,
              None
            )
          )

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

  "ContactPreferencesController.updateRouteShow" when {

    "a journey can be retrieved from the backend" when {

      "the user is authorised" should {

        "the user has a pre selected preference but no preference in session" should {

          lazy val result: Future[Result] = TestContactPreferencesController.updateRouteShow(journeyId)(fakeRequest)

          "return an OK (200)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(individual)
            mockGetContactPreference(regimeModel)(Right(digitalPreferenceModel))

            verifyExplicitAudit(
              ViewContactPreferenceAuditModel.auditType,
              ViewContactPreferenceAuditModel(
                journeyModelMax.regime,
                None,
                journeyModelMax.email,
                journeyModelMax.address,
                Some(Email)
              )
            )

            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "display the correct page with the correct option selected" in {
            title(result) shouldBe pageTitle
            selectElement(result, "#email").hasAttr("checked") shouldBe true
            selectElement(result, "#letter").hasAttr("checked") shouldBe false
          }
        }

        "the user has a pre selected preference but the preference in session override it" should {

          lazy val result: Future[Result] = TestContactPreferencesController.updateRouteShow(journeyId)(
            fakeRequest.withSession(SessionKeys.preference -> Letter.value)
          )

          "return an OK (200)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(individual)
            mockGetContactPreference(regimeModel)(Right(digitalPreferenceModel))

            verifyExplicitAudit(
              ViewContactPreferenceAuditModel.auditType,
              ViewContactPreferenceAuditModel(
                journeyModelMax.regime,
                None,
                journeyModelMax.email,
                journeyModelMax.address,
                Some(Email)
              )
            )

            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "display the correct page with the correct option selected" in {
            title(result) shouldBe pageTitle
            selectElement(result, "#email").hasAttr("checked") shouldBe false
            selectElement(result, "#letter").hasAttr("checked") shouldBe true
          }
        }

        "the user does NOT have a pre selected preference" should {

          lazy val result: Future[Result] = TestContactPreferencesController.updateRouteShow(journeyId)(fakeRequest)

          "return a ISE (500)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(individual)
            mockGetContactPreference(regimeModel)(Left(GetContactPreferenceHttpParser.NotFound))

            status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }
      }

      "the user is NOT authorised" should {

        lazy val result: Future[Result] = TestContactPreferencesController.updateRouteShow(journeyId)(fakeRequest)

        "return an FORBIDDEN (403)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthorise(individual, retrievals)(Future.failed(InsufficientEnrolments()))

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

    "the backend indicates the user is unauthorised" when {

      lazy val result: Future[Result] = TestContactPreferencesController.updateRouteShow(journeyId)(fakeRequest)

      "return SEE_OTHER (303)" in {
        mockJourney(journeyId)(Left(Unauthorised))

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(appConfig.signInUrl())
      }
    }
  }

  "ContactPreferencesController.setRouteSubmit" when {

    "a journey can be retrieved from the backend" when {

      "the user is authorised" when {

        "'Email' option is entered" should {

          lazy val result = TestContactPreferencesController.setRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
            ContactPreferencesForm.preference -> PreferenceMapping.option_email
          ))

          "return an SEE_OTHER (303) status" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(EmptyPredicate)

            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect to the Confirm Preferences set route" in {
            redirectLocation(result) shouldBe Some(controllers.routes.ConfirmPreferencesController.setRouteShow(journeyId).url)
          }

          "have the preference added to session as Email" in {
            session(result).get(SessionKeys.preference) shouldBe Some(Email.value)
          }
        }

        "'Letter' option is entered" should {

          lazy val result = TestContactPreferencesController.setRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
            ContactPreferencesForm.preference -> PreferenceMapping.option_letter
          ))

          "return an SEE_OTHER (303) status" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(EmptyPredicate)

            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect to the Confirm Preferences set route" in {
            redirectLocation(result) shouldBe Some(controllers.routes.ConfirmPreferencesController.setRouteShow(journeyId).url)
          }

          "have the preference added to session as Letter" in {
            session(result).get(SessionKeys.preference) shouldBe Some(Letter.value)
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
            ContactPreferencesForm.preference -> PreferenceMapping.option_letter
          ))

          status(result) shouldBe Status.FORBIDDEN
        }
      }
    }

    "a journey can NOT be retrieved from the backend" when {

      "return an NOT_FOUND (404)" in {
        mockJourney(journeyId)(Left(NotFound))

        val result = TestContactPreferencesController.setRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
          ContactPreferencesForm.preference -> PreferenceMapping.option_email
        ))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "ContactPreferencesController.updateRouteSubmit" when {

    "a journey can be retrieved from the backend" when {

      "the user is authorised" when {

        "'Email' option is entered" when {

          lazy val result = TestContactPreferencesController.updateRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
            ContactPreferencesForm.preference -> PreferenceMapping.option_email
          ))

          "return an SEE_OTHER (303) status" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(individual)

            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect to the confirm preference update route" in {
            redirectLocation(result) shouldBe Some(controllers.routes.ConfirmPreferencesController.updateRouteShow(journeyId).url)
          }

          "have the preference added to session as Email" in {
            session(result).get(SessionKeys.preference) shouldBe Some(Email.value)
          }
        }

        "'Letter' option is entered" when {

          lazy val result = TestContactPreferencesController.updateRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
            ContactPreferencesForm.preference -> PreferenceMapping.option_letter
          ))

          "return an SEE_OTHER (303) status" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(individual)

            status(result) shouldBe Status.SEE_OTHER
          }

          "redirect to the confirm preference update route" in {
            redirectLocation(result) shouldBe Some(controllers.routes.ConfirmPreferencesController.updateRouteShow(journeyId).url)
          }
          "add the preference to session set to Letter" in {
            session(result).get(SessionKeys.preference) shouldBe Some(Letter.value)
          }
        }

        "no radio option is selected" should {

          "return a BAD_REQUEST (400)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(individual)

            val result = TestContactPreferencesController.updateRouteSubmit(journeyId)(FakeRequest("POST", "/"))

            status(result) shouldBe Status.BAD_REQUEST
          }
        }
      }

      "the user is NOT authorised" should {

        "return an FORBIDDEN (403)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthorise(individual, retrievals)(Future.failed(InsufficientEnrolments()))

          val result = TestContactPreferencesController.updateRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
            ContactPreferencesForm.preference -> PreferenceMapping.option_letter
          ))

          status(result) shouldBe Status.FORBIDDEN
        }
      }
    }

    "a journey can NOT be retrieved from the backend" when {

      "return an NOT_FOUND (404)" in {
        mockJourney(journeyId)(Left(NotFound))

        val result = TestContactPreferencesController.updateRouteSubmit(journeyId)(FakeRequest("POST", "/").withFormUrlEncodedBody(
          ContactPreferencesForm.preference -> PreferenceMapping.option_email
        ))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
