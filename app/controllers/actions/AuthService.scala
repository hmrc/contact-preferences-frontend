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

import config.Constants
import javax.inject.{Inject, Singleton}
import models.RegimeModel
import models.requests.User
import play.api.Logger
import play.api.mvc._
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core.{NoActiveSession, _}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class AuthService @Inject()(val authConnector: AuthConnector) extends FrontendController with AuthorisedFunctions {

  private def delegatedAuthRule(regime: RegimeModel): Enrolment =
    Enrolment(regime.`type`.enrolmentId)
      .withIdentifier(regime.identifier.key.value, regime.identifier.value)
      .withDelegatedAuthRule(regime.`type`.delegatedAuthRule)

  private val arn: Enrolments => Option[String] = _.getEnrolment(Constants.AgentServicesEnrolment) flatMap {
    _.getIdentifier(Constants.AgentServicesReference).map(_.value)
  }

  def authorise(regime: RegimeModel)(f: User[_] => Future[Result])(implicit request : Request[_]): Future[Result] =
    authorised(delegatedAuthRule(regime)).retrieve(Retrievals.allEnrolments) {
      enrolments =>
        f(User(regime.identifier.value, arn(enrolments))(request))
    } recover {
      case _: NoActiveSession =>
        Logger.debug(s"[ContactPreferencesAuthorised][async] - User has no active session, unauthorised")
        Unauthorized
      case _: AuthorisationException =>
        Logger.debug(s"[ContactPreferencesAuthorised][async] - User has an active session, but does not have sufficient authority")
        Forbidden
  }

}
