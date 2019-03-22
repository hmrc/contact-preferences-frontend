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

package testOnly.forms

import models._
import play.api.data.Form
import play.api.data.Forms.{optional, _}
import testOnly.models.SimulateJourneyModel


object SimulateJourneyForm {

  val submitToDes: String = "submitToDes"
  val regime: String = "regime"
  val regimeId: String = "regimeId"
  val regimeIdValue: String = "regimeIdValue"
  val email: String = "email"
  val continueUrl: String = "continueUrl"
  val serviceName: String = "serviceName"
  val addressLine1: String = "addressLine1"
  val addressLine2: String = "addressLine2"
  val addressLine3: String = "addressLine3"
  val addressLine4: String = "addressLine4"
  val postcode: String = "postcode"
  val countryCode: String = "countryCode"

  val simulateJourneyForm: Form[SimulateJourneyModel] = Form(
    mapping(
      submitToDes -> boolean,
      regime -> nonEmptyText,
      regimeId -> nonEmptyText,
      regimeIdValue -> nonEmptyText,
      email -> nonEmptyText,
      continueUrl -> nonEmptyText,
      serviceName -> optional(text),
      addressLine1 -> nonEmptyText,
      addressLine2 -> nonEmptyText,
      addressLine3 -> optional(text),
      addressLine4 -> optional(text),
      postcode -> optional(text),
      countryCode -> nonEmptyText
    )(customApply)(customUnapply)
  )

  private def customApply(submitToDes: Boolean,
                           regime: String,
                          regimeId: String,
                          regimeIdValue: String,
                          email: String,
                          continueUrl: String,
                          serviceName: Option[String],
                          addressLine1: String,
                          addressLine2: String,
                          addressLine3: Option[String],
                          addressLine4: Option[String],
                          postcode: Option[String],
                          countryCode: String): SimulateJourneyModel = {

    val regimeModel = RegimeModel(
      RegimeType(regime), Id(Identifier(regimeId), regimeIdValue)
    )
    SimulateJourneyModel(
      submitToDes = submitToDes,
      regime = regimeModel,
      serviceName = serviceName,
      continueUrl = continueUrl,
      email = email,
      address = AddressModel(
        line1 = addressLine1,
        line2 = addressLine2,
        line3 = addressLine3,
        line4 = addressLine4,
        postcode = postcode,
        countryCode = countryCode
      ))
  }

  private def customUnapply(arg: SimulateJourneyModel):
  Option[(Boolean, String, String, String, String, String, Option[String], String, String, Option[String], Option[String], Option[String], String)] =
    Some(
      (
        arg.submitToDes,
        arg.regime.`type`.id,
        arg.regime.identifier.key.value,
        arg.regime.identifier.value,
        arg.email,
        arg.continueUrl,
        arg.serviceName,
        arg.address.line1,
        arg.address.line2,
        arg.address.line3,
        arg.address.line4,
        arg.address.postcode,
        arg.address.countryCode
      )
    )

}
