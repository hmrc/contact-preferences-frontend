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

package testOnly.controllers

import config.{AppConfig, ErrorHandler}
import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import testOnly.connectors.ContactPreferencesConnector
import testOnly.connectors.httpParsers.SimulateJourneySubmitHttpParser.Success
import testOnly.forms.SimulateJourneyForm.simulateJourneyForm
import testOnly.views.html.{simulate_journey_start, simulate_journey_success}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class SimulateJourneyController @Inject()(contactPreferenceConnector: ContactPreferencesConnector,
                                          errorHandler: ErrorHandler,
                                          val messagesApi: MessagesApi,
                                          implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val startJourneyShow: Action[AnyContent] = Action { implicit request =>
    Ok(simulate_journey_start(simulateJourneyForm, testOnly.controllers.routes.SimulateJourneyController.startJourneySubmit()))
  }

  def success(preferenceId: Option[String]): Action[AnyContent] = Action.async { implicit request =>
    preferenceId.fold(Future.successful(BadRequest("No preferenceId Query String provided!!!"))) { id =>
      contactPreferenceConnector.getPreference(id).map {
        case Right(data) => Ok(simulate_journey_success(data))
        case Left(err) => InternalServerError(errorHandler.standardErrorTemplate(
          "Error","Unexpected Error",s"Status: ${err.status} Message: ${err.body}"
        ))
      }
    }
  }

  val startJourneySubmit: Action[AnyContent] = Action.async { implicit request =>
    simulateJourneyForm.bindFromRequest.fold(
      formWithErrors =>
        Future.successful(BadRequest(simulate_journey_start(formWithErrors, testOnly.controllers.routes.SimulateJourneyController.startJourneySubmit()))),
      data => {
        contactPreferenceConnector.startJourney(data).map {
          case Right(Success(url)) => Redirect(url)
          case Left(err) => InternalServerError(errorHandler.standardErrorTemplate(
            "Error","Unexpected Error",s"Status: ${err.status} Message: ${err.body}"
          ))
        }
      }
    )
  }
}
