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

package models

import play.api.libs.json._
import utils.JsonSugar

sealed trait Preference {
  val value: String
}

case object Email extends Preference {
  override val value = "DIGITAL"
}

case object Letter extends Preference {
  override val value = "PAPER"
}

object Preference extends JsonSugar {

  implicit val reads: Reads[Preference] =
    __.read[String] map apply

  implicit val writes: Writes[Preference] = Writes {
    preference => JsString(preference.value)
  }

  def apply(value: String): Preference = value.toUpperCase match {
    case Email.value => Email
    case Letter.value => Letter
    case x => throw jsonError(__ \ "preference", s"Invalid Preference: $x. Valid Preference set: (${Email.value}|${Letter.value})")
  }

  def unapply: Preference => String = _.value
}

