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

package views

import assets.AddressTestConstants
import assets.messages.{CommonMessages, ContactPreferencesMessages}
import assets.JourneyTestConstants.{journeyId, journeyModelMax}
import controllers.routes
import forms.ContactPreferencesForm
import _root_.utils.ViewTestUtils
import config.SessionKeys
import models.{Email, Letter}


class ContactPreferencesViewSpec extends ViewTestUtils {

  object Selectors {
    val pageHeading = "h1"
    val currentPreference = s"#content > article p"
    val radioEmail = "label[for='email']"
    val radioEmailHint = "label[for='email'] span"
    val radioLetter = "label[for='letter']"
    val radioLetterHint = "label[for='letter'] span"
    val continue = "#continue-button"

    val errorHeading = "#error-summary-display > ul > li > a"
    val error = "#error-message-preference"
  }

  "The Contact Preferences page" when {

    "the page has no errors and user is currently email" should {

      lazy val document = parseView(views.html.contact_preferences(
        contactPreferencesForm = ContactPreferencesForm.contactPreferencesForm,
        email = journeyModelMax.email,
        postAction = routes.ContactPreferencesController.setRouteSubmit(journeyId),
        address = AddressTestConstants.addressModelMax,
        currentPreference = Some(Email)
      ))

      s"have the correct document title" in {
        document.title shouldBe ContactPreferencesMessages.fullTitle
      }

      s"have a the correct page heading" in {
        document.select(Selectors.pageHeading).text() shouldBe ContactPreferencesMessages.title
      }

      "have a paragraph indicating that the current preference is email" in {
        document.select(Selectors.currentPreference).text shouldBe ContactPreferencesMessages.email
      }

      s"have a the correct Email Option" in {
        document.select(Selectors.radioEmail).text() shouldBe ContactPreferencesMessages.radioEmail + " " + journeyModelMax.email
      }

      s"have a the correct Email Option hint" in {
        document.select(Selectors.radioEmailHint).text() shouldBe journeyModelMax.email
      }

      s"have the correct Letter Option" in {
        document.select(Selectors.radioLetter).text() shouldBe ContactPreferencesMessages.radioLetter + " " + journeyModelMax.address.singleLineAddress
      }

      s"have the correct Letter Option hint" in {
        document.select(Selectors.radioLetterHint).text() shouldBe journeyModelMax.address.singleLineAddress
      }

      s"have a the correct continue button" in {
        document.select(Selectors.continue).attr("value") shouldBe CommonMessages.continue
      }
    }

    "the page has no errors and user is currently letter" should {

      lazy val document = parseView(views.html.contact_preferences(
        contactPreferencesForm = ContactPreferencesForm.contactPreferencesForm,
        email = journeyModelMax.email,
        postAction = routes.ContactPreferencesController.setRouteSubmit(journeyId),
        address = AddressTestConstants.addressModelMax,
        currentPreference = Some(Letter)
      ))

      "have a paragraph indicating that the current preference is letter" in {
        document.select(Selectors.currentPreference).text shouldBe ContactPreferencesMessages.letter
      }
    }

    "the page has errors" should {

      lazy val document = parseView(views.html.contact_preferences(
        ContactPreferencesForm.contactPreferencesForm.bind(Map(SessionKeys.preference -> "")),
        journeyModelMax.email,
        routes.ContactPreferencesController.setRouteSubmit(journeyId),
        address = AddressTestConstants.addressModelMax
      ))

      s"have the correct document title" in {
        document.title shouldBe s"${CommonMessages.error} ${ContactPreferencesMessages.fullTitle}"
      }

      s"have a the correct page heading" in {
        document.select(Selectors.pageHeading).text() shouldBe ContactPreferencesMessages.title
      }

      s"have a the correct page error heading" in {
        document.select(Selectors.errorHeading).text() shouldBe ContactPreferencesMessages.errorSummary
      }

      s"have a the correct page error" in {
        document.select(Selectors.error).text() shouldBe ContactPreferencesMessages.errorSummary
      }
    }
  }
}
