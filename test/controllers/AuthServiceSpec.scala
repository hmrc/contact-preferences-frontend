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

import assets.JourneyTestConstants.regimeModel
import connectors.mocks.MockAuthConnector
import controllers.actions.AuthService
import play.api.http.Status._
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import uk.gov.hmrc.auth.core.{InsufficientEnrolments, MissingBearerToken}

import scala.concurrent.Future


class AuthServiceSpec extends MockAuthConnector {

  object TestContactPreferencesAuthorised extends AuthService(mockAuthConnector, appConfig)

  def result: Future[Result] = TestContactPreferencesAuthorised.authorise(regimeModel) {
    implicit user =>
      Future.successful(Ok)
  }

  "The ContactPreferencesAuthorised.async method" should {

    "For a Principal User" when {

      "an authorised result is returned from the Auth Connector" should {

        "Successfully authenticate and process the request" in {
          mockAuthenticated(EmptyPredicate)
          status(result) shouldBe OK
        }
      }
    }

    "For an Agent User" when {

      "they are Signed Up to MTD ContactPreferences" should {

        "Successfully authenticate and process the request" in {
          mockAuthRetrieveAgentServicesEnrolled(EmptyPredicate)
          status(result) shouldBe OK
        }
      }
    }

    "For any type of user" when {

      "a NoActiveSession exception is returned from the Auth Connector" should {

        "Return a SEE_OTHER response" in {
          mockAuthorise(EmptyPredicate, retrievals)(Future.failed(MissingBearerToken()))
          status(result) shouldBe SEE_OTHER
        }

        "Redirect to GG Sign In" in {
          mockAuthorise(EmptyPredicate, retrievals)(Future.failed(MissingBearerToken()))
          redirectLocation(result) shouldBe Some(appConfig.signInUrl())
        }
      }

      "an InsufficientAuthority exception is returned from the Auth Connector" should {

        "Return a forbidden response" in {
          mockAuthorise(EmptyPredicate, retrievals)(Future.failed(InsufficientEnrolments()))
          status(result) shouldBe FORBIDDEN
        }
      }
    }
  }
}
