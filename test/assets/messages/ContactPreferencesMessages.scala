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

  val title = "Does the business want to receive VAT email notifications?"
  val text1: String => String = email => s"When the business has a new message about VAT in its HMRC account, we can let you know by sending an email to $email"

  val text2 = "You may continue to receive letters in the post for a while. These will be sent to your principal place of business."
  val text3 = "For security reasons, we do not include any information or links in the email itself. Sign in to your HMRC account to read the content of the message."
  val radioYes = "Yes, send email notifications"
  val radioNo = "No, send me letters only"
  val errorSummary = "Choose yes or no, if you are ready to set your contact preferences"

}
