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
import assets.messages.ConfirmPreferencesMessages.{title => pageTitle}
import audit.mocks.MockAuditConnector
import audit.models.ContactPreferenceAuditModel
import config.SessionKeys
import connectors.httpParsers.JourneyHttpParser.{NotFound, Unauthorised}
import connectors.httpParsers.{StoreContactPreferenceHttpParser, UpdateContactPreferenceHttpParser}
import controllers.mocks.MockAuthService
import models.{Email, Letter}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services.mocks.{MockContactPreferencesService, MockJourneyService}
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import utils.ControllerTestUtils

import scala.concurrent.Future

class ConfirmPreferencesControllerSpec extends ControllerTestUtils with MockContactPreferencesService
  with MockJourneyService with MockAuthService with MockAuditConnector {

  object TestConfirmPreferencesController extends ConfirmPreferencesController(
    messagesApi = messagesApi,
    authService = mockAuthService,
    journeyService = mockJourneyService,
    preferenceService = mockContactPreferencesService,
    auditConnector = mockAuditConnector,
    errorHandler = errorHandler,
    appConfig = appConfig
  )

  "ConfirmPreferencesController.setRouteShow" when {

    "a Email preference has been stored in session" when {

      lazy val request = fakeRequest.withSession(
        SessionKeys.preference -> Email.value
      )

      "a journey can be retrieved from the backend" when {

        "the user is authorised" should {

          lazy val result: Future[Result] = TestConfirmPreferencesController.setRouteShow(journeyId)(request)

          "return an OK (200)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(EmptyPredicate)

            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "display the correct page with the correct option selected" in {
            title(result) shouldBe pageTitle
          }
        }

        "the user is NOT authorised" should {

          lazy val result: Future[Result] = TestConfirmPreferencesController.setRouteShow(journeyId)(request)

          "return an FORBIDDEN (403)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthorise(EmptyPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

            status(result) shouldBe Status.FORBIDDEN
          }
        }
      }

      "a journey can NOT be retrieved from the backend" should {

        lazy val result: Future[Result] = TestConfirmPreferencesController.setRouteShow(journeyId)(request)

        "return an INTERNAL SERVER ERROR (500)" in {
          mockJourney(journeyId)(Left(NotFound))

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the backend indicates the user is unauthorised" when {

        lazy val result: Future[Result] = TestConfirmPreferencesController.setRouteShow(journeyId)(request)

        "return SEE_OTHER (303)" in {
          mockJourney(journeyId)(Left(Unauthorised))

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(appConfig.signInUrl())
        }
      }
    }

    "a Letter preference has been stored in session" should {

      lazy val result: Future[Result] = TestConfirmPreferencesController.setRouteShow(journeyId)(fakeRequest.withSession(
        SessionKeys.preference -> Letter.value
      ))

      "return a OK (200)" in {

        mockJourney(journeyId)(Right(journeyModelMax))
        mockAuthenticated(EmptyPredicate)

        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

      "display the correct page with the correct option selected" in {
        title(result) shouldBe pageTitle
      }
    }

    "a preference is not in session" should {

      lazy val result: Future[Result] = TestConfirmPreferencesController.setRouteShow(journeyId)(fakeRequest)

      "return Redirect to " in {

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.ContactPreferencesController.setRouteShow(journeyId).url)
      }
    }
  }

  "ConfirmPreferencesController.updateRouteShow" when {

    "a Email preference has been stored in session" when {

      lazy val request = fakeRequest.withSession(
        SessionKeys.preference -> Email.value
      )

      "a journey can be retrieved from the backend" when {

        "the user is authorised" should {

          lazy val result: Future[Result] = TestConfirmPreferencesController.updateRouteShow(journeyId)(request)

          "return an OK (200)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(EmptyPredicate)

            status(result) shouldBe Status.OK
          }

          "return HTML" in {
            contentType(result) shouldBe Some("text/html")
            charset(result) shouldBe Some("utf-8")
          }

          "display the correct page with the correct option selected" in {
            title(result) shouldBe pageTitle
          }
        }

        "the user is NOT authorised" should {

          lazy val result: Future[Result] = TestConfirmPreferencesController.updateRouteShow(journeyId)(request)

          "return an FORBIDDEN (403)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthorise(EmptyPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

            status(result) shouldBe Status.FORBIDDEN
          }
        }
      }

      "a journey can NOT be retrieved from the backend" should {

        lazy val result: Future[Result] = TestConfirmPreferencesController.updateRouteShow(journeyId)(request)

        "return an INTERNAL SERVER ERROR (500)" in {
          mockJourney(journeyId)(Left(NotFound))

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "the backend indicates the user is unauthorised" when {

        lazy val result: Future[Result] = TestConfirmPreferencesController.updateRouteShow(journeyId)(request)

        "return SEE_OTHER (303)" in {
          mockJourney(journeyId)(Left(Unauthorised))

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(appConfig.signInUrl())
        }
      }
    }

    "a Letter preference has been stored in session" should {

      lazy val result: Future[Result] = TestConfirmPreferencesController.updateRouteShow(journeyId)(fakeRequest.withSession(
        SessionKeys.preference -> Letter.value
      ))

      "return a OK (200)" in {

        mockJourney(journeyId)(Right(journeyModelMax))
        mockAuthenticated(EmptyPredicate)

        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

      "display the correct page with the correct option selected" in {
        title(result) shouldBe pageTitle
      }
    }

    "a preference is not in session" should {

      lazy val result: Future[Result] = TestConfirmPreferencesController.updateRouteShow(journeyId)(fakeRequest)

      "return a SEE_OTHER (303)" in {

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.ContactPreferencesController.updateRouteShow(journeyId).url)
      }
    }
  }

  "ConfirmPreferencesController.setRouteSubmit" when {

    "a journey can be retrieved from the backend" when {

      "the user is authorised" when {

        "a preference has been stored in session" should {

          lazy val request = fakeRequest.withSession(
            SessionKeys.preference -> Email.value
          )

          lazy val result: Future[Result] = TestConfirmPreferencesController.setRouteSubmit(journeyId)(request)

          "return a SEE_OTHER (303)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(EmptyPredicate)
            mockStoreJourneyPreference(journeyId, Email)(Right(StoreContactPreferenceHttpParser.Success))

            verifyExplicitAudit(
              ContactPreferenceAuditModel.auditType,
              ContactPreferenceAuditModel(
                journeyModelMax.regime,
                None,
                journeyModelMax.email,
                Email
              )
            )

            status(result) shouldBe Status.SEE_OTHER
            redirectLocation(result) shouldBe Some(s"${journeyModelMax.continueUrl}?preferenceId=$journeyId")
          }
        }

        "a preference has NOT been stored in session" should {

          lazy val result: Future[Result] = TestConfirmPreferencesController.setRouteSubmit(journeyId)(fakeRequest)

          "return a SEE_OTHER (303)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(EmptyPredicate)

            status(result) shouldBe Status.SEE_OTHER
            redirectLocation(result) shouldBe Some(controllers.routes.ContactPreferencesController.setRouteShow(journeyId).url)
          }
        }
      }

      "the user is NOT authorised" should {

        lazy val result: Future[Result] = TestConfirmPreferencesController.setRouteSubmit(journeyId)(fakeRequest)

        "return an FORBIDDEN (403)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthorise(EmptyPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

          status(result) shouldBe Status.FORBIDDEN
        }
      }
    }

    "a journey can NOT be retrieved from the backend" should {

      lazy val result: Future[Result] = TestConfirmPreferencesController.setRouteSubmit(journeyId)(fakeRequest)

      "return an INTERNAL SERVER ERROR (500)" in {
        mockJourney(journeyId)(Left(NotFound))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "ConfirmPreferencesController.updateRouteSubmit" when {

    "a journey can be retrieved from the backend" when {

      "the user is authorised" when {

        "a preference has been stored in session" should {

          lazy val request = fakeRequest.withSession(
            SessionKeys.preference -> Email.value
          )

          lazy val result: Future[Result] = TestConfirmPreferencesController.updateRouteSubmit(journeyId)(request)

          "return a SEE_OTHER (303)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(EmptyPredicate)
            mockUpdateJourneyPreference(journeyModelMax.regime, Email)(Right(UpdateContactPreferenceHttpParser.Success))

            verifyExplicitAudit(
              ContactPreferenceAuditModel.auditType,
              ContactPreferenceAuditModel(
                journeyModelMax.regime,
                None,
                journeyModelMax.email,
                Email
              )
            )

            status(result) shouldBe Status.SEE_OTHER
            redirectLocation(result) shouldBe Some(s"${journeyModelMax.continueUrl}?preferenceId=$journeyId")
          }
        }

        "a preference has NOT been stored in session" should {

          lazy val result: Future[Result] = TestConfirmPreferencesController.updateRouteSubmit(journeyId)(fakeRequest)

          "return a SEE_OTHER (303)" in {
            mockJourney(journeyId)(Right(journeyModelMax))
            mockAuthenticated(EmptyPredicate)

            status(result) shouldBe Status.SEE_OTHER
            redirectLocation(result) shouldBe Some(controllers.routes.ContactPreferencesController.updateRouteShow(journeyId).url)
          }
        }
      }

      "the user is NOT authorised" should {

        lazy val result: Future[Result] = TestConfirmPreferencesController.updateRouteSubmit(journeyId)(fakeRequest)

        "return an FORBIDDEN (403)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthorise(EmptyPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

          status(result) shouldBe Status.FORBIDDEN
        }
      }
    }

    "a journey can NOT be retrieved from the backend" should {

      lazy val result: Future[Result] = TestConfirmPreferencesController.updateRouteSubmit(journeyId)(fakeRequest)

      "return an INTERNAL SERVER ERROR (500)" in {
        mockJourney(journeyId)(Left(NotFound))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
