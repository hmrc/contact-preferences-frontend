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

package utils

import org.jsoup.Jsoup
import org.jsoup.select.Elements
import play.api.mvc.Result

import scala.language.implicitConversions

trait ControllerTestUtils extends TestUtils {

  def title(result: Result): String = {
    Jsoup.parse(bodyOf(result)).select("h1").text()
  }

  def selectElement(result: Result, id: String): Elements = {
    Jsoup.parse(bodyOf(result)).select(id)
  }
}
