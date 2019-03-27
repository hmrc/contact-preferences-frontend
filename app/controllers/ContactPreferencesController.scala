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
import config.{AppConfig, ErrorHandler, SessionKeys}
import connectors.httpParsers.JourneyHttpParser.Unauthorised
import controllers.actions.AuthService
import forms.ContactPreferencesForm._
import javax.inject.{Inject, Singleton}
import models._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.twirl.api.HtmlFormat
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
        Future.successful(Ok(displayPage(
          form = contactPreferencesForm,
          journeyModel = journeyModel,
          postAction = routes.ContactPreferencesController.setRouteSubmit(id),
          selectedPreference = determineSelectedPreference(None)
        )))
      }
    }
  }

  val setRouteSubmit: String => Action[AnyContent] = id => Action.async { implicit request =>
    getJourneyContext(id) { journeyModel =>
      authService.authorise(journeyModel.regime) { user =>
        contactPreferencesForm.bindFromRequest.fold(
          formWithErrors => Future.successful(
            BadRequest(displayPage(formWithErrors, journeyModel, routes.ContactPreferencesController.setRouteSubmit(id)))
          ),
          preference => {
            auditConnector.sendExplicitAudit(
              ContactPreferenceAuditModel.auditType,
              ContactPreferenceAuditModel(journeyModel.regime, user.arn, journeyModel.email, preference)
            )
            preferenceService.storeJourneyPreference(id, preference).map {
              case Right(_) => Redirect(controllers.routes.ConfirmPreferencesController.setRouteShow(id))
                  .addingToSession(SessionKeys.preference -> preference.value)
              case Left(_) => errorHandler.showInternalServerError
            }
          }
        )
      }
    }
  }


  val updateRouteShow: String => Action[AnyContent] = id => Action.async { implicit request =>
    val postAction = routes.ContactPreferencesController.updateRouteSubmit(id)
    getJourneyContext(id) { journeyModel =>
      authService.authorise(journeyModel.regime) { _ =>
        preferenceService.getContactPreference(journeyModel.regime).map {
          case Right(preferenceModel) =>
            Ok(displayPage(
              form = contactPreferencesForm,
              journeyModel = journeyModel,
              postAction = postAction,
              selectedPreference = determineSelectedPreference(Some(preferenceModel.preference)),
              currentPreference = Some(preferenceModel.preference)
            ))
          case _ => errorHandler.showInternalServerError
        }
      }
    }
  }

  val updateRouteSubmit: String => Action[AnyContent] = id => Action.async { implicit request =>
    getJourneyContext(id) { journeyModel =>
      authService.authorise(journeyModel.regime) { user =>
        contactPreferencesForm.bindFromRequest.fold(
          formWithErrors => Future.successful(
            BadRequest(displayPage(formWithErrors, journeyModel, routes.ContactPreferencesController.updateRouteShow(id)))
          ),
          preference => {
            auditConnector.sendExplicitAudit(
              ContactPreferenceAuditModel.auditType,
              ContactPreferenceAuditModel(journeyModel.regime, user.arn, journeyModel.email, preference)
            )
            preferenceService.updateContactPreference(journeyModel.regime, preference).map {
              case Right(_) => Redirect(controllers.routes.ConfirmPreferencesController.updateRouteShow(id))
                .addingToSession(SessionKeys.preference -> preference.value)
              case Left(_) => errorHandler.showInternalServerError
            }
          }
        )
      }
    }
  }

  private def determineSelectedPreference(currentPreference: Option[Preference])(implicit request: Request[_]): Option[Preference] =
    request.session.get(SessionKeys.preference).fold(currentPreference)(x => Some(Preference(x)))

  private def displayPage(form: Form[Preference],
                  journeyModel: Journey,
                  postAction: Call,
                  selectedPreference: Option[Preference] = None,
                  currentPreference: Option[Preference] = None)
                 (implicit request: Request[_]): HtmlFormat.Appendable = {
    contact_preferences(
      serviceName = journeyModel.serviceName,
      contactPreferencesForm = selectedPreference.fold(form)(form.fill),
      email = journeyModel.email,
      address = journeyModel.address,
      currentPreference = currentPreference,
      postAction = postAction
    )
  }

  private def getJourneyContext(id: String)(f: Journey => Future[Result])(implicit request: Request[_]): Future[Result] = {
    journeyService.getJourney(id) flatMap {
      case Right(journeyModel) => f(journeyModel)
      case Left(Unauthorised) => Future.successful(Redirect(appConfig.signInUrl()))
      case Left(_) => Future.successful(errorHandler.showInternalServerError)
    }
  }
}
