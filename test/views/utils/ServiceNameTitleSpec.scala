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

package views.utils

import _root_.utils.TestUtils
import assets.messages.{CommonMessages, ContactPreferencesMessages}

class ServiceNameTitleSpec extends TestUtils {

  "ServiceNameTitle" when {

    "given only a message for the title" should {

      "return the correct full title without an error" in {

        val expectedResult = ContactPreferencesMessages.fullTitle
        val actualResult = ServiceNameTitle.fullTitle("contactPreferences.title")

        actualResult shouldBe expectedResult
      }
    }

    "given a message for the title as well as an error" should {

      "return the correct full title without an error" in {

        val expectedResult = s"${CommonMessages.error} ${ContactPreferencesMessages.fullTitle}"
        val actualResult = ServiceNameTitle.fullTitle("contactPreferences.title", true)

        actualResult shouldBe expectedResult
      }
    }
  }
}