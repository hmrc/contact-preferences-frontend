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

import assets.ContactPreferencesTestConstants.digitalPreferenceModel
import assets.JourneyTestConstants._
import connectors.httpParsers.{StoreContactPreferenceHttpParser => StoreHttpParser}
import connectors.httpParsers.{GetContactPreferenceHttpParser => GetHttpParser}
import connectors.mocks.MockContactPreferencesConnector
import models.Email

class ContactPreferencesServiceSpec extends MockContactPreferencesConnector {

  object TestContactPreferencesService extends ContactPreferencesService(connector)

  "PreferenceService" should {

    "return a Journey model when getJourney is successful" in {

      mockStoreContactPreference("testID", Email)(Right(StoreHttpParser.Success))

      val actualResult = await(TestContactPreferencesService.storeJourneyPreference("testID", Email))
      val expectedResult = Right(StoreHttpParser.Success)

      actualResult shouldBe expectedResult
    }

    "return an Error model when getJourney is unsuccessful" in {

      mockStoreContactPreference("testID", Email)(Left(StoreHttpParser.InvalidPreferencePayload))

      val actualResult = await(TestContactPreferencesService.storeJourneyPreference("testID", Email))
      val expectedResult = Left(StoreHttpParser.InvalidPreferencePayload)

      actualResult shouldBe expectedResult
    }
  }

  "ContactPreferencesService" should {

    "return a Contact Preference model when getContactPreference is successful" in {

      mockGetContactPreference(regimeModel)(Right(digitalPreferenceModel))
      val actualResult = await(TestContactPreferencesService.getContactPreference(regimeModel))
      val expectedResult = Right(digitalPreferenceModel)

      actualResult shouldBe expectedResult
    }

    "return an Error model when getContactPreference is unsuccessful" in {

      mockGetContactPreference(regimeModel)(Left(GetHttpParser.InvalidJson))
      val actualResult = await(TestContactPreferencesService.getContactPreference(regimeModel))
      val expectedResult = Left(GetHttpParser.InvalidJson)

      actualResult shouldBe expectedResult
    }
  }
}
