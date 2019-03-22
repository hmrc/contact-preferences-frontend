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

import assets.messages.{CommonMessages, ContactPreferencesMessages}
import assets.JourneyTestConstants.{journeyId,journeyModelMax}
import controllers.routes
import forms.ContactPreferencesForm
import utils.ViewTestUtils


class ContactPreferencesViewSpec extends ViewTestUtils {

  object Selectors {
    val pageHeading = "h1"
    val text = (number: Int) => s"#content > article p:nth-child($number)"
    val radioYes = "#yes_no > div:nth-child(4) > label"
    val radioNo = "#yes_no > div:nth-child(5) > label"
    val continue = "#continue-button"

    val errorHeading = "#error-summary-display > ul > li > a"
    val error = "#error-message-yes_no"
  }


  "The software choices search page" when {

    "the page has no errors" should {

      lazy val document = parseView(views.html.contact_preferences(
        ContactPreferencesForm.contactPreferencesForm,
        journeyModelMax.email,
        routes.ContactPreferencesController.setRouteSubmit(journeyId))
      )

      s"have the correct document title" in {
        document.title shouldBe ContactPreferencesMessages.title
      }

      s"have a the correct page heading" in {
        document.select(Selectors.pageHeading).text() shouldBe ContactPreferencesMessages.title
      }

      s"have the correct first paragraph text" in {
        document.select(Selectors.text(2)).text() shouldBe ContactPreferencesMessages.text1
      }

      s"have a the correct second paragraph text" in {
        document.select(Selectors.text(3)).text() shouldBe ContactPreferencesMessages.text2
      }

      s"have a the correct Yes Option" in {
        document.select(Selectors.radioYes).text() shouldBe ContactPreferencesMessages.radioYes(journeyModelMax.email)
      }

      s"have the correct No Option" in {
        document.select(Selectors.radioNo).text() shouldBe ContactPreferencesMessages.radioNo
      }

      s"have a the correct continue button" in {
        document.select(Selectors.continue).attr("value") shouldBe CommonMessages.continue
      }
    }

    "the page has errors" should {

      lazy val document = parseView(views.html.contact_preferences(
        ContactPreferencesForm.contactPreferencesForm.bind(Map("yes_no" -> "")),
        journeyModelMax.email,
        routes.ContactPreferencesController.setRouteSubmit(journeyId))
      )

      s"have the correct document title" in {
        document.title shouldBe s"${CommonMessages.error} ${ContactPreferencesMessages.title}"
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
