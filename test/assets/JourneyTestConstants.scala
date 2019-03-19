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
import play.api.libs.json.{JsObject, JsString, Json}
import assets.BaseTestConstants._

object JourneyTestConstants {

  val journeyId = "abcjdjd-121414-ascacfsf"

  val identifierJson = JsString(VRN.value)

  val idJson = Json.obj(
    "key" -> identifierJson,
    "value" -> testVatNumber
  )

  val regimeTypeJson: JsString = JsString(MTDVAT.id)

  val regimeJson = Json.obj(
    "type" -> regimeTypeJson,
    "identifier" -> idJson
  )

  val continueUrl = "continue/url"
  val email = "email@email.com"
  val serviceName = "Service Name"

  val journeyJsonMax: JsObject = Json.obj(
    "regime" -> regimeJson,
    "serviceName" -> serviceName,
    "continueUrl" -> continueUrl,
    "email" -> email
  )

  val journeyJsonMin: JsObject = Json.obj(
    "regime" -> regimeJson,
    "continueUrl" -> continueUrl
  )

  val idModel = Id(VRN, testVatNumber)

  val regimeModel = RegimeModel(MTDVAT, idModel)

  val journeyModelMax = Journey(
    regime = regimeModel,
    serviceName = Some(serviceName),
    continueUrl = continueUrl,
    email = Some(email)
  )

  val journeyModelMin =  Journey(
    regime = regimeModel,
    continueUrl = continueUrl
  )

}
