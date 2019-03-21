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

import config.{AppConfig, ErrorHandler}
import controllers.actions.AuthService
import forms.ContactPreferencesForm._
import javax.inject.{Inject, Singleton}
import models._
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.{ContactPreferencesService, JourneyService}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.contact_preferences

import scala.concurrent.Future

@Singleton
class ContactPreferencesDesController @Inject()(val messagesApi: MessagesApi,
                                                authService: AuthService,
                                                journeyService: JourneyService,
                                                contactPreferencesService: ContactPreferencesService,
                                                errorHandler: ErrorHandler,
                                                auditConnector: AuditConnector,
                                                implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val show: String => Action[AnyContent] = id => Action.async { implicit request =>
    getJourneyContext(id) { journeyModel =>
      authService.authorise(journeyModel.regime) { _ =>

        lazy val displayPage = (form: Form[YesNo]) => Ok(contact_preferences(
          serviceName = journeyModel.serviceName,
          contactPreferencesForm = form,
          email = journeyModel.email,
          postAction = routes.ContactPreferencesController.submit(id)
        ))

        contactPreferencesService.getContactPreference(journeyModel.regime).map {
          case Right(ContactPreferenceModel(Digital)) =>
            Logger.debug("DIGITAL PREFERENCE | DIGITAL PREFERENCE | DIGITAL PREFERENCE")
            displayPage(contactPreferencesForm.fill(Yes))
          case Right(ContactPreferenceModel(Paper)) =>
            displayPage(contactPreferencesForm.fill(No))
          case _ =>
            displayPage(contactPreferencesForm)
        }
      }
    }
  }

  private def getJourneyContext(id: String)(f: Journey => Future[Result])(implicit request: Request[_]): Future[Result] = {
    journeyService.getJourney(id) flatMap {
      case Right(journeyModel) => f(journeyModel)
      case Left(_) => Future.successful(errorHandler.showInternalServerError)
    }
  }
}