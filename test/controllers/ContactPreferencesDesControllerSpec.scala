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
import assets.JourneyTestConstants._
import assets.messages.ContactPreferencesMessages.{title => pageTitle}
import audit.mocks.MockAuditConnector
import connectors.httpParsers.JourneyHttpParser.NotFound
import controllers.mocks.MockAuthService
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services.mocks.{MockContactPreferencesDesService, MockJourneyService}
import uk.gov.hmrc.auth.core.InsufficientEnrolments
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import utils.ControllerTestUtils

import scala.concurrent.Future

class ContactPreferencesDesControllerSpec extends ControllerTestUtils with MockContactPreferencesDesService
  with MockJourneyService with MockAuthService with MockAuditConnector {

  object TestContactPreferencesDesController extends ContactPreferencesDesController(
    messagesApi,
    mockAuthService,
    mockJourneyService,
    mockContactPreferencesDesService,
    errorHandler,
    mockAuditConnector,
    appConfig
  )

  "ContactPreferencesController.show" when {

    "a journey can be retrieved from the backend" when {

      "the user is authorised" should {

        "the user has a pre selected preference" should {

          lazy val result: Future[Result] = TestContactPreferencesDesController.show(journeyId)(fakeRequest)

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
          }
        }

        "the user does NOT have a pre selected preference" should {

          lazy val result: Future[Result] = TestContactPreferencesDesController.show(journeyId)(fakeRequest)

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

          "display the correct page with no option selected" in {
            title(result) shouldBe pageTitle
            selectElement(result, "#yes").hasAttr("checked") shouldBe false
            selectElement(result, "#no").hasAttr("checked") shouldBe false
          }
        }
      }

      "the user is NOT authorised" should {

        lazy val result: Future[Result] = TestContactPreferencesDesController.show(journeyId)(fakeRequest)

        "return an FORBIDDEN (403)" in {
          mockJourney(journeyId)(Right(journeyModelMax))
          mockAuthorise(EmptyPredicate, retrievals)(Future.failed(InsufficientEnrolments()))

          status(result) shouldBe Status.FORBIDDEN
        }
      }
    }

    "a journey can NOT be retrieved from the backend" when {

      lazy val result: Future[Result] = TestContactPreferencesDesController.show(journeyId)(fakeRequest)

      "return an NOT_FOUND (404)" in {
        mockJourney(journeyId)(Left(NotFound))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
