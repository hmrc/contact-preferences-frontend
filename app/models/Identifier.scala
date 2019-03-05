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

trait Identifier {
  val value: String
}

object Identifier {
  implicit val reads: Reads[Identifier] = __.read[String] map apply
  implicit val writes: Writes[Identifier] = Writes { identifier =>
    JsString(unapply(identifier))
  }

  def apply(args: String): Identifier = args match {
    case VRN.value => VRN
    case x => throw JsResultException(Seq(__ -> Seq(ValidationError(Seq(s"Invalid Identifier: $x, valid identifiers: ${VRN.value}")))))
  }

  def unapply(arg: Identifier): String = arg match {
    case _ => VRN.value
  }
}

object VRN extends Identifier {
  override val value: String = "vrn"
}
