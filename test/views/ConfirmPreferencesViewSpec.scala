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
    val text = "#content > article > p:nth-child(2)"
    val link = "#content > article > p:nth-child(3) > a"
    val confirmAndContinue = "input"
  }

  "The confirm preferences page" should {

    lazy val document = parseView(views.html.confirm_preferences(
      journeyModelMax.email,
      routes.ContactPreferencesController.updateRouteShow(journeyId), //TODO change this to the ConfirmPreferencesController.submit when created
      routes.ContactPreferencesController.updateRouteShow(journeyId).url
    ))

    s"have the correct document title" in {
      document.title shouldBe ConfirmPreferencesMessages.title
    }

    s"have a the correct page heading" in {
      document.select(Selectors.pageHeading).text() shouldBe ConfirmPreferencesMessages.title
    }

    s"have the correct paragraph text" in {
      document.select(Selectors.text).text() shouldBe s"${ConfirmPreferencesMessages.text} ${journeyModelMax.email}"
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
