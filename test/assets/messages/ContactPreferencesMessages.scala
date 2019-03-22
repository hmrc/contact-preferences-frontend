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

object ContactPreferencesMessages {

  val title = "Does the business want to receive emails about VAT?"
  val text1 = "We can email you when the business has a new message about VAT in their HMRC account."
  val text2 = "You may continue to receive letters to your principal place of business for a while."
  def radioYes(email: String): String = s"Yes, send emails to $email"
  val radioNo = "No, send me letters only"
  val errorSummary = "Choose yes or no, if you are ready to set your contact preferences"

}
