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

import assets.messages.TimeoutMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import _root_.utils.ViewTestUtils

class SessionTimeoutSpec extends ViewTestUtils {

  "Rendering the session timeout page" should {

    object Selectors {
      val pageHeading = "#content h1"
      val instructions = "#content p"
    }


    lazy val view = views.html.session_timeout()(fakeRequest, messages, appConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe TimeoutMessages.title
    }

    s"have a the correct page heading" in {
      document.select(Selectors.pageHeading).text shouldBe TimeoutMessages.h1
    }

    s"have the correct instructions on the page" in {
      document.select(Selectors.instructions).text shouldBe TimeoutMessages.p1
    }

  }
}
