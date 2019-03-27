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

import audit.models.ViewContactPreferenceAuditModel
import config.{AppConfig, ErrorHandler, SessionKeys}
import controllers.actions.AuthService
import forms.ContactPreferencesForm._
import javax.inject.{Inject, Singleton}
import models._
import models.requests.User
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc._
import play.twirl.api.HtmlFormat
import services.{ContactPreferencesService, JourneyService}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import views.html.contact_preferences

import scala.concurrent.Future

@Singleton
class ContactPreferencesController @Inject()(val messagesApi: MessagesApi,
                                             authService: AuthService,
                                             val journeyService: JourneyService,
                                             preferenceService: ContactPreferencesService,
                                             val errorHandler: ErrorHandler,
                                             auditConnector: AuditConnector,
                                             implicit val appConfig: AppConfig) extends BaseController {

  val setRouteShow: String => Action[AnyContent] = id => Action.async { implicit request =>
    getJourneyContext(id) { journeyModel =>
      authService.authorisedNoPredicate(journeyModel.regime) { user =>
        auditPreference(journeyModel)(user)
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
      authService.authorisedNoPredicate(journeyModel.regime) { _ =>
        Future.successful(contactPreferencesForm.bindFromRequest.fold(
          formWithErrors =>
            BadRequest(displayPage(formWithErrors, journeyModel, routes.ContactPreferencesController.setRouteSubmit(id))
          ),
          preference => Redirect(controllers.routes.ConfirmPreferencesController.setRouteShow(id))
            .addingToSession(SessionKeys.preference -> preference.value)
        ))
      }
    }
  }


  val updateRouteShow: String => Action[AnyContent] = id => Action.async { implicit request =>
    val postAction = routes.ContactPreferencesController.updateRouteSubmit(id)
    getJourneyContext(id) { journeyModel =>
      authService.authorisedWithEnrolmentPredicate(journeyModel.regime) { user =>
        preferenceService.getContactPreference(journeyModel.regime).map {
          case Right(preferenceModel) => {
            auditPreference(journeyModel, Some(preferenceModel.preference))(user)
            Ok(displayPage(
              form = contactPreferencesForm,
              journeyModel = journeyModel,
              postAction = postAction,
              selectedPreference = determineSelectedPreference(Some(preferenceModel.preference)),
              currentPreference = Some(preferenceModel.preference)
            ))
          }
          case _ => errorHandler.showInternalServerError
        }
      }
    }
  }

  val updateRouteSubmit: String => Action[AnyContent] = id => Action.async { implicit request =>
    getJourneyContext(id) { journeyModel =>
      authService.authorisedWithEnrolmentPredicate(journeyModel.regime) { user =>
        Future.successful(contactPreferencesForm.bindFromRequest.fold(
          formWithErrors =>
            BadRequest(displayPage(formWithErrors, journeyModel, routes.ContactPreferencesController.updateRouteShow(id))
          ),
          preference =>
            Redirect(controllers.routes.ConfirmPreferencesController.updateRouteShow(id))
              .addingToSession(SessionKeys.preference -> preference.value)
        ))
      }
    }
  }

  private def auditPreference(journey: Journey, preference: Option[Preference] = None)(implicit user: User[_]): Unit =
    auditConnector.sendExplicitAudit(
      ViewContactPreferenceAuditModel.auditType,
      ViewContactPreferenceAuditModel(journey.regime, user.arn, journey.email, journey.address, preference)
    )

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
}
