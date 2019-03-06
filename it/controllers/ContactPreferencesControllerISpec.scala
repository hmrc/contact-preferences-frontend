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
import forms.ContactPreferencesForm
import models.Yes
import play.api.http.Status._
import stubs.{AuthStub, ContactPreferencesStub}
import utils.ITUtils


class ContactPreferencesControllerISpec extends ITUtils {

  "GET /:journeyId" when {

    "getJourney is successful" should {

      "show the Contact Preferences page" in {

        ContactPreferencesStub.getJourneySuccess(journeyId)(journeyJson)
        AuthStub.authorisedIndividual()

        val res = await(get(s"/$journeyId"))

        res should have {
          httpStatus(OK)
        }
      }
    }

    "getJourney is unsuccessul" should {

      "show an internal server error" in {

        ContactPreferencesStub.getJourneyFailed(journeyId)

        val res = await(get(s"/$journeyId"))

        res should have {
          httpStatus(INTERNAL_SERVER_ERROR)
        }
      }
    }
  }

  "POST /:journeyId" when {

    "getJourney is successful" when {

      "storePreference is successul" should {

        "redirect to continueUrl" in {

          ContactPreferencesStub.getJourneySuccess(journeyId)(journeyJson)
          AuthStub.authorisedIndividual()
          ContactPreferencesStub.storePreferenceSuccess(journeyId)

          val res = await(post(s"/$journeyId")(toFormData(ContactPreferencesForm.contactPreferencesForm, Yes)))

          res should have {
            httpStatus(SEE_OTHER)
          }
        }
      }

      "storePreference is unsuccessul" should {

        "show an internal server error" in {

          ContactPreferencesStub.getJourneySuccess(journeyId)(journeyJson)
          AuthStub.authorisedIndividual()
          ContactPreferencesStub.storePreferenceFailed(journeyId)

          val res = await(post(s"/$journeyId")(toFormData(ContactPreferencesForm.contactPreferencesForm, Yes)))

          res should have {
            httpStatus(INTERNAL_SERVER_ERROR)
          }
        }
      }
    }

    "getJourney is unsuccessul" should {

      "show an internal server error" in {

        ContactPreferencesStub.getJourneyFailed(journeyId)

        val res = await(post(s"/$journeyId")(toFormData(ContactPreferencesForm.contactPreferencesForm, Yes)))

        res should have {
          httpStatus(INTERNAL_SERVER_ERROR)
        }
      }
    }
  }
}
