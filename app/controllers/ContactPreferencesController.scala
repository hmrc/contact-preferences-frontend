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

package controllers


import config.AppConfig
import controllers.actions.AuthService
import forms.ContactPreferencesForm._
import javax.inject.{Inject, Singleton}
import models.{Journey, No, Yes}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.contact_preferences

import scala.concurrent.Future

@Singleton
class ContactPreferencesController @Inject()(val messagesApi: MessagesApi,
                                             authService: AuthService,
                                             journeyService: JourneyService,
                                             implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val show: String => Action[AnyContent] = id => Action.async { implicit request =>
    getJourneyContext(id) { journeyModel =>
      authService.authorise(journeyModel.regime) { _ =>
        Future.successful(Ok(contact_preferences(contactPreferencesForm, routes.ContactPreferencesController.submit(id))))
      }
    }
  }

  val submit: String => Action[AnyContent] = id => Action.async { implicit request =>
    getJourneyContext(id) { journeyModel =>
      authService.authorise(journeyModel.regime) { _ =>
        Future.successful(contactPreferencesForm.bindFromRequest.fold(
          formWithErrors =>
            BadRequest(contact_preferences(formWithErrors, routes.ContactPreferencesController.submit(id)))
          ,{
            case Yes =>
              //TODO
              NotImplemented
            case No =>
              //TODO
              NotImplemented
          }
        ))
      }
    }
  }

  private def getJourneyContext(id: String)(f: Journey => Future[Result])(implicit request: Request[_]): Future[Result] = {
    journeyService.getJourney(id) flatMap {
      case Right(journeyModel) => f(journeyModel)
      case Left(errorModel) => Future.successful(Status(errorModel.status)(errorModel.body))
    }
  }
}
