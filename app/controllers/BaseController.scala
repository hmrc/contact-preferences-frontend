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

import config.{AppConfig, ErrorHandler, SessionKeys}
import connectors.httpParsers.JourneyHttpParser.Unauthorised
import javax.inject.Singleton
import models._
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.JourneyService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
trait BaseController extends FrontendController with I18nSupport {

  val journeyService: JourneyService
  val errorHandler: ErrorHandler

  def getJourneyContext(id: String)(f: Journey => Future[Result])(implicit request: Request[_], appConfig: AppConfig): Future[Result] = {
    journeyService.getJourney(id) flatMap {
      case Right(journeyModel) => f(journeyModel)
      case Left(Unauthorised) => Future.successful(Redirect(appConfig.signInUrl()))
      case Left(_) => Future.successful(errorHandler.showInternalServerError)
    }
  }

  def getPreferenceFromSession(changeUrl: String)(f: Preference => Future[Result])(implicit request: Request[_]): Future[Result] = {
    request.session.get(SessionKeys.preference) match {
      case Some(preference) => f(Preference(preference))
      case _ => Future.successful(Redirect(changeUrl))
    }
  }
}
