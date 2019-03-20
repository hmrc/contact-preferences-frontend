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

package services

import assets.JourneyTestConstants._
import connectors.httpParsers.GetDesContactPreferenceHttpParser.InvalidJson
import connectors.mocks.MockContactPreferencesDesConnector
import assets.ContactPreferencesTestConstants._

class ContactPreferencesDesServiceSpec extends MockContactPreferencesDesConnector {

  object TestContactPreferencesDesService extends ContactPreferencesDesService(connector)

  "ContactPreferencesDesService" should {

    "return a Contact Preference model when getContactPreference is successful" in {

      mockGetContactPreferenceDes(regimeModel)(Right(digitalPreferenceModel))
      val actualResult = await(TestContactPreferencesDesService.getContactPreference(regimeModel))
      val expectedResult = Right(digitalPreferenceModel)

      actualResult shouldBe expectedResult
    }

    "return an Error model when getContactPreference is unsuccessful" in {

      mockGetContactPreferenceDes(regimeModel)(Left(InvalidJson))
      val actualResult = await(TestContactPreferencesDesService.getContactPreference(regimeModel))
      val expectedResult = Left(InvalidJson)

      actualResult shouldBe expectedResult
    }
  }
}
