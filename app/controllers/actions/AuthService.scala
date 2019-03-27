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

package controllers.actions

import config.{AppConfig, Constants}
import javax.inject.{Inject, Singleton}
import models.RegimeModel
import models.requests.User
import play.api.Logger
import play.api.i18n.Messages
import play.api.mvc._
import uk.gov.hmrc.auth.core.authorise.{EmptyPredicate, Predicate}
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core.{NoActiveSession, _}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthService @Inject()(val authConnector: AuthConnector, implicit val appConfig: AppConfig)
  extends FrontendController with AuthorisedFunctions {

  private def enrolmentCheck(regime: RegimeModel): Enrolment =
    Enrolment(regime.`type`.enrolmentId)
      .withIdentifier(regime.identifier.key.value, regime.identifier.value)

  private val arn: Enrolments => Option[String] = _.getEnrolment(Constants.AgentServicesEnrolment) flatMap {
    _.getIdentifier(Constants.AgentServicesReference).map(_.value)
  }

  private def authorised(regime: RegimeModel, predicate: Predicate)(f: User[_] => Future[Result])
               (implicit request : Request[_], messages: Messages): Future[Result] =
    authorised(predicate).retrieve(Retrievals.allEnrolments) {
      enrolments =>
        f(User(regime.identifier.value, arn(enrolments))(request))
    } recover {
      case _: NoActiveSession =>
        Logger.debug(s"[ContactPreferencesAuthorised][async] - User has no active session, unauthorised")
        Redirect(appConfig.signInUrl())
      case _: AuthorisationException =>
        Logger.debug(s"[ContactPreferencesAuthorised][async] - User has an active session, but does not have sufficient authority")
        Forbidden(views.html.unauthorised())
    }

  def authorisedWithEnrolmentPredicate(regimeModel: RegimeModel)(f: User[_] => Future[Result])
                                      (implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[_], messages: Messages): Future[Result] = {
    authorised(regimeModel, enrolmentCheck(regimeModel))(f)
  }

  def authorisedNoPredicate(regimeModel: RegimeModel)(f: User[_] => Future[Result])
                           (implicit hc: HeaderCarrier, ec: ExecutionContext, request: Request[_], messages: Messages): Future[Result] = {
    authorised(regimeModel, EmptyPredicate)(f)
  }

}
