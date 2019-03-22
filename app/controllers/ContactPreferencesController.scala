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

import audit.models.ContactPreferenceAuditModel
import config.{AppConfig, ErrorHandler}
import controllers.actions.AuthService
import forms.ContactPreferencesForm._
import javax.inject.{Inject, Singleton}
import models._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{ContactPreferencesService, JourneyService}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.contact_preferences

import scala.concurrent.Future

@Singleton
class ContactPreferencesController @Inject()(val messagesApi: MessagesApi,
                                             authService: AuthService,
                                             journeyService: JourneyService,
                                             preferenceService: ContactPreferencesService,
                                             errorHandler: ErrorHandler,
                                             auditConnector: AuditConnector,
                                             implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val setRouteShow: String => Action[AnyContent] = id => Action.async { implicit request =>
    getJourneyContext(id) { journeyModel =>
      authService.authorise(journeyModel.regime) { _ =>
        Future.successful(displayPage(contactPreferencesForm, journeyModel, routes.ContactPreferencesController.setRouteSubmit(id)))
      }
    }
  }

  val updateRouteShow: String => Action[AnyContent] = id => Action.async { implicit request =>
    val postAction = routes.ContactPreferencesController.setRouteSubmit(id)
    // val postAction = routes.ContactPreferencesController.updateRouteSubmit(id) TODO use this when updateRouteSubmit is created
    getJourneyContext(id) { journeyModel =>
      authService.authorise(journeyModel.regime) { _ =>
        preferenceService.getContactPreference(journeyModel.regime).map {
          case Right(ContactPreferenceModel(Digital)) =>
            displayPage(contactPreferencesForm.fill(Yes), journeyModel, postAction)
          case Right(ContactPreferenceModel(Paper)) =>
            displayPage(contactPreferencesForm.fill(No), journeyModel, postAction)
          case _ =>
            displayPage(contactPreferencesForm, journeyModel, postAction)
        }
      }
    }
  }

  def displayPage(form: Form[YesNo], journeyModel: Journey, postAction: Call)
                 (implicit request: Request[_]): Result = {
    Ok(contact_preferences(
      serviceName = journeyModel.serviceName,
      contactPreferencesForm = form,
      email = journeyModel.email,
      postAction = postAction
    ))
  }

  val setRouteSubmit: String => Action[AnyContent] = id => Action.async { implicit request =>
    getJourneyContext(id) { journeyModel =>
      authService.authorise(journeyModel.regime) { user =>
        contactPreferencesForm.bindFromRequest.fold(
          formWithErrors => Future.successful(BadRequest(contact_preferences(
            formWithErrors, journeyModel.email, routes.ContactPreferencesController.setRouteSubmit(id), journeyModel.serviceName)
          )),
          answer => {
            val preference = if (answer == Yes) Digital else Paper
            auditConnector.sendExplicitAudit(
              ContactPreferenceAuditModel.auditType,
              ContactPreferenceAuditModel(journeyModel.regime, user.arn, journeyModel.email, preference)
            )
            preferenceService.storeJourneyPreference(id, preference).map {
              case Right(_) => Redirect(journeyModel.continueUrl, Map("preferenceId" -> Seq(id)))
              case Left(_) => errorHandler.showInternalServerError
            }
          }
        )
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
