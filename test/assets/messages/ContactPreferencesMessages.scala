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

package assets.messages

import assets.messages.CommonMessages._

object ContactPreferencesMessages {

  val title = "Does the business want to receive emails about VAT?"
  val fullTitle = s"$title - $serviceName - $govUk"
  val text1 = "We can email you when the business has a new message about VAT in their HMRC account."
  val radioEmail = "Email"
  val radioLetter = "Letter"
  val errorSummary = "Choose email or letter, if you are ready to set your contact preferences"

}
