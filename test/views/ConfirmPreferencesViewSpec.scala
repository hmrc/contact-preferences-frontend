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

import assets.JourneyTestConstants.{journeyId, journeyModelMax}
import assets.messages.{CommonMessages, ConfirmPreferencesMessages}
import controllers.routes
import _root_.utils.ViewTestUtils


class ConfirmPreferencesViewSpec extends ViewTestUtils {

  object Selectors {
    val pageHeading = "h1"
    val text1 = "#content > article > p:nth-child(2)"
    val text2 = "#content > article > p:nth-child(3)"
    val link = "#content > article > p.form-group > a"
    val confirmAndContinue = "input"
  }

  "The confirm preferences page" when {

    "given an email preference" should {

      lazy val document = parseView(views.html.confirm_preferences(
        journey = journeyModelMax,
        digitalPreference = true,
        postAction = routes.ContactPreferencesController.updateRouteShow(journeyId),
        changeUrl = routes.ContactPreferencesController.updateRouteShow(journeyId).url
      ))

      s"have the correct document title" in {
        document.title shouldBe ConfirmPreferencesMessages.title
      }

      s"have a the correct page heading" in {
        document.select(Selectors.pageHeading).text() shouldBe ConfirmPreferencesMessages.title
      }

      s"have the correct first paragraph text" in {
        document.select(Selectors.text1).text() shouldBe ConfirmPreferencesMessages.textEmail
      }

      s"have the correct paragraph text for email" in {
        document.select(Selectors.text2).text() shouldBe journeyModelMax.email
      }

      s"have a the correct link with the correct text" in {
        document.select(Selectors.link).text() shouldBe ConfirmPreferencesMessages.link
        document.select(Selectors.link).attr("href") shouldBe routes.ContactPreferencesController.updateRouteShow(journeyId).url
      }

      s"have a the correct continue button" in {
        document.select(Selectors.confirmAndContinue).attr("value") shouldBe CommonMessages.confirmAndContinue
      }
    }

    "given an letter preference" should {

      lazy val document = parseView(views.html.confirm_preferences(
        journey = journeyModelMax,
        digitalPreference = false,
        postAction = routes.ContactPreferencesController.updateRouteShow(journeyId),
        changeUrl = routes.ContactPreferencesController.updateRouteShow(journeyId).url
      ))

      s"have the correct document title" in {
        document.title shouldBe ConfirmPreferencesMessages.title
      }

      s"have a the correct page heading" in {
        document.select(Selectors.pageHeading).text() shouldBe ConfirmPreferencesMessages.title
      }

      s"have the correct first paragraph text" in {
        document.select(Selectors.text1).text() shouldBe ConfirmPreferencesMessages.textLetter
      }

      s"have the correct paragraph text for letter" in {
        val address = journeyModelMax.address
        document.select(Selectors.text2).text() shouldBe
          s"${address.line1} ${address.line2} ${address.line3.get} ${address.line4.get} ${address.postcode.get} ${address.countryCode}"
      }

      s"have a the correct link with the correct text" in {
        document.select(Selectors.link).text() shouldBe ConfirmPreferencesMessages.link
        document.select(Selectors.link).attr("href") shouldBe routes.ContactPreferencesController.updateRouteShow(journeyId).url
      }

      s"have a the correct continue button" in {
        document.select(Selectors.confirmAndContinue).attr("value") shouldBe CommonMessages.confirmAndContinue
      }
    }
  }
}
