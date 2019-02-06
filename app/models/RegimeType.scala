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

import play.api.data.validation.ValidationError
import play.api.libs.json._

sealed trait RegimeType {
  val id: String
  val enrolmentId: String
  val delegatedAuthRule: String
  val desId: String
}

object RegimeType {
  implicit val reads: Reads[RegimeType] = __.read[String] map apply
  implicit val writes: Writes[RegimeType] = Writes { regimeType =>
    JsString(unapply(regimeType))
  }

  def apply(arg: String): RegimeType = arg.toUpperCase match {
    case MTDVAT.id => MTDVAT
    case x => throw JsResultException(Seq(__ -> Seq(ValidationError(Seq(s"Invalid regime type: $x, valid regime type: TODO : Add list of all valid regime types")))))
  }

  def unapply(arg: RegimeType): String = arg match {
    case MTDVAT => MTDVAT.id
  }
}

object MTDVAT extends RegimeType {
  override val id: String = "VAT"
  override val enrolmentId: String = "HMRC-MTD-VAT"
  override val delegatedAuthRule: String = "mtd-vat-auth"
  override val desId: String = "VATC"
}
