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

import assets.JourneyITConstants._
import config.SessionKeys
import forms.ContactPreferencesForm
import models.Email
import play.api.http.Status._
import stubs.{AuthStub, ContactPreferencesStub}
import utils.ITUtils


class ConfirmPreferencesControllerISpec extends ITUtils {

  "GET /set/confirm-preference/:journeyId" when {

    "getJourney is successful" should {

      "show the Contact Preferences page" in {

        ContactPreferencesStub.startJourneySuccess(journeyId)(journeyJson)
        AuthStub.authorisedIndividual()

        val res = await(get(s"/set/confirm-preference/$journeyId", Map(SessionKeys.preference -> Email.value)))

        res should have {
          httpStatus(OK)
        }
      }
    }

    "getJourney is unsuccessul" should {

      "show an internal server error" in {

        ContactPreferencesStub.startJourneyFailed(journeyId)

        val res = await(get(s"/set/confirm-preference/$journeyId", Map(SessionKeys.preference -> Email.value)))

        res should have {
          httpStatus(INTERNAL_SERVER_ERROR)
        }
      }
    }
  }

  "GET /update/confirm-preference/:journeyId" when {

    "getJourney is successful" should {

      "show the Contact Preferences page" in {

        ContactPreferencesStub.startJourneySuccess(journeyId)(journeyJson)
        AuthStub.authorisedIndividual()

        val res = await(get(s"/update/confirm-preference/$journeyId", Map(SessionKeys.preference -> Email.value)))

        res should have {
          httpStatus(OK)
        }
      }
    }

    "getJourney is unsuccessul" should {

      "show an internal server error" in {

        ContactPreferencesStub.startJourneyFailed(journeyId)

        val res = await(get(s"/update/confirm-preference/$journeyId", Map(SessionKeys.preference -> Email.value)))

        res should have {
          httpStatus(INTERNAL_SERVER_ERROR)
        }
      }
    }
  }

  "POST /set/confirm-preference/:journeyId" when {

    "getJourney is successful" when {

      "redirect to continueUrl" in {

        ContactPreferencesStub.startJourneySuccess(journeyId)(journeyJson)
        AuthStub.authorisedIndividual()

        val res = await(post(s"/set/confirm-preference/$journeyId", Map(SessionKeys.preference -> Email.value))(
          toFormData(ContactPreferencesForm.contactPreferencesForm, Email)
        ))

        res should have {
          httpStatus(SEE_OTHER)
          continueUrl("continue/url")
        }
      }
    }

    "getJourney is unsuccessul" should {

      "show an internal server error" in {

        ContactPreferencesStub.startJourneyFailed(journeyId)

        val res = await(post(s"/set/confirm-preference/$journeyId", Map(SessionKeys.preference -> Email.value))(
          toFormData(ContactPreferencesForm.contactPreferencesForm, Email)
        ))

        res should have {
          httpStatus(INTERNAL_SERVER_ERROR)
        }
      }
    }
  }

  "POST /update/confirm-preference/:journeyId" when {

    "getJourney is successful" when {

      "redirect to continueUrl" in {

        ContactPreferencesStub.startJourneySuccess(journeyId)(journeyJson)
        AuthStub.authorisedIndividual()

        val res = await(post(s"/update/confirm-preference/$journeyId", Map(SessionKeys.preference -> Email.value))(
          toFormData(ContactPreferencesForm.contactPreferencesForm, Email)
        ))

        res should have {
          httpStatus(SEE_OTHER)
          continueUrl("continue/url")
        }
      }
    }

    "getJourney is unsuccessul" should {

      "show an internal server error" in {

        ContactPreferencesStub.startJourneyFailed(journeyId)

        val res = await(post(s"/update/confirm-preference/$journeyId", Map(SessionKeys.preference -> Email.value))(
          toFormData(ContactPreferencesForm.contactPreferencesForm, Email)
        ))

        res should have {
          httpStatus(INTERNAL_SERVER_ERROR)
        }
      }
    }
  }
}
