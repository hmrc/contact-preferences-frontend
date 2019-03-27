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
import config.{AppConfig, ErrorHandler, QueryStringKeys, SessionKeys}
import connectors.httpParsers.JourneyHttpParser.Unauthorised
import controllers.actions.AuthService
import javax.inject.{Inject, Singleton}
import models._
import models.requests.User
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, _}
import services.{ContactPreferencesService, JourneyService}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.confirm_preferences

import scala.concurrent.Future

@Singleton
class ConfirmPreferencesController @Inject()(val messagesApi: MessagesApi,
                                             authService: AuthService,
                                             journeyService: JourneyService,
                                             preferenceService: ContactPreferencesService,
                                             auditConnector: AuditConnector,
                                             errorHandler: ErrorHandler,
                                             implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  val setRouteShow: String => Action[AnyContent] = id => Action.async { implicit request =>
    displayPage(
      id = id,
      postAction = controllers.routes.ConfirmPreferencesController.setRouteSubmit(id),
      changeUrl = controllers.routes.ContactPreferencesController.setRouteShow(id).url
    )
  }

  val setRouteSubmit: String => Action[AnyContent] = id => Action.async { implicit requests =>
    getJourneyContext(id) { journeyModel =>
      authService.authorise(journeyModel.regime) { user =>
        getPreferenceFromSession(controllers.routes.ContactPreferencesController.setRouteShow(id).url) { preference =>
          auditPreference(journeyModel, preference)(user)
          preferenceService.storeJourneyPreference(id, preference).map {
            case Right(_) => Redirect(journeyModel.continueUrl, Map(QueryStringKeys.preferenceId -> Seq(id)))
            case Left(_) => errorHandler.showInternalServerError
          }
        }
      }
    }
  }

  val updateRouteShow: String => Action[AnyContent] = id => Action.async { implicit request =>
    displayPage(
      id = id,
      postAction = controllers.routes.ConfirmPreferencesController.updateRouteSubmit(id),
      changeUrl = controllers.routes.ContactPreferencesController.updateRouteShow(id).url
    )
  }

  val updateRouteSubmit: String => Action[AnyContent] = id => Action.async { implicit requests =>
    getJourneyContext(id) { journeyModel =>
      authService.authorise(journeyModel.regime) { user =>
        getPreferenceFromSession(controllers.routes.ContactPreferencesController.updateRouteShow(id).url) { preference =>
          auditPreference(journeyModel, preference)(user)
          preferenceService.updateContactPreference(journeyModel.regime, preference) map {
            case Right(_) => Redirect(journeyModel.continueUrl, Map(QueryStringKeys.preferenceId -> Seq(id)))
            case Left(_) => errorHandler.showInternalServerError
          }
        }
      }
    }
  }

  private def displayPage(id: String, postAction: Call, changeUrl: String)(implicit request: Request[_]): Future[Result] = {
    getPreferenceFromSession(changeUrl) { preference =>
      getJourneyContext(id) { journeyModel =>
        authService.authorise(journeyModel.regime) { _ =>
          Future.successful(Ok(confirm_preferences(
            journey = journeyModel,
            digitalPreference = preference == Email,
            postAction = postAction,
            changeUrl = changeUrl
          )))
        }
      }
    }
  }

  private def auditPreference(journey: Journey, preference: Preference)(implicit user: User[_]): Unit =
    auditConnector.sendExplicitAudit(
      ContactPreferenceAuditModel.auditType,
      ContactPreferenceAuditModel(journey.regime, user.arn, journey.email, preference)
    )

  private def getJourneyContext(id: String)(f: Journey => Future[Result])(implicit request: Request[_]): Future[Result] = {
    journeyService.getJourney(id) flatMap {
      case Right(journeyModel) => f(journeyModel)
      case Left(Unauthorised) => Future.successful(Redirect(appConfig.signInUrl()))
      case Left(_) => Future.successful(errorHandler.showInternalServerError)
    }
  }

  private def getPreferenceFromSession(changeUrl: String)(f: Preference => Future[Result])(implicit request: Request[_]): Future[Result] = {
    request.session.get(SessionKeys.preference) match {
      case Some(preference) => f(Preference(preference))
      case _ => Future.successful(Redirect(changeUrl))
    }
  }
}