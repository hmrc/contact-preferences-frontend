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

package forms

import models._
import play.api.data.FormError
import play.api.data.format.Formatter

object PreferenceMapping {

  val option_email: String = "email"

  val option_letter: String = "letter"

  def preferenceMapping(error: String): Formatter[Preference] = new Formatter[Preference] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Preference] = {
      data.get(key) match {
        case Some(`option_email`) => Right(Email)
        case Some(`option_letter`) => Right(Letter)
        case _ => Left(Seq(FormError(key, error)))
      }
    }

    override def unbind(key: String, value: Preference): Map[String, String] = {
      val stringValue = value match {
        case Email => option_email
        case Letter => option_letter
      }

      Map(key -> stringValue)
    }
  }
}
