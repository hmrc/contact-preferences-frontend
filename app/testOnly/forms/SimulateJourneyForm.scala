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
import play.api.data.Forms._


object SimulateJourneyForm {

  val regime: String = "regime"
  val regimeId: String = "regimeId"
  val regimeIdValue: String = "regimeIdValue"
  val email: String = "email"
  val continueUrl: String = "continueUrl"

  val simulateJourneyForm: Form[Journey] = Form(
    mapping(
      regime -> nonEmptyText,
      regimeId -> nonEmptyText,
      regimeIdValue -> nonEmptyText,
      email -> optional(text),
      continueUrl -> nonEmptyText
    )(customApply)(customUnapply)
  )

  private def customApply(regime: String, regimeId: String, regimeIdValue: String, email: Option[String], continueUrl: String): Journey = {
    val regimeModel = RegimeModel(
      RegimeType(regime), Id(Identifier(regimeId), regimeIdValue)
    )
    new Journey(regimeModel, continueUrl, email)
  }

  private def customUnapply(arg: Journey): Option[(String, String, String, Option[String], String)] = Some(
    (arg.regime.`type`.id, arg.regime.identifier.key.value, arg.regime.identifier.value, arg.email, arg.continueUrl)
  )

}
