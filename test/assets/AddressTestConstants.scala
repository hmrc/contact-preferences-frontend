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

package assets

import models._
import play.api.libs.json.{JsObject, Json}

object AddressTestConstants {

  val line1 = "line1"
  val line2 = "line2"
  val line3 = "line3"
  val line4 = "line4"
  val postcode = "postcode"
  val countryCode = "countryCode"

  val addressJsonMax: JsObject = Json.obj(
    "line1" -> line1,
    "line2" -> line2,
    "line3" -> line3,
    "line4" -> line4,
    "postcode" -> postcode,
    "countryCode" -> countryCode
  )

  val addressJsonMin: JsObject = Json.obj(
    "line1" -> line1,
    "line2" -> line2,
    "countryCode" -> countryCode
  )

  val addressModelMax = AddressModel(
    line1 = "line1",
    line2 = "line2",
    line3 = Some("line3"),
    line4 = Some("line4"),
    postcode = Some("postcode"),
    countryCode = "countryCode"
  )

  val addressModelMin = AddressModel(
    line1 = "line1",
    line2 = "line2",
    countryCode = "countryCode"
  )

}