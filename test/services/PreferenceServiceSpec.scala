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
import connectors.httpParsers.StoreContactPreferenceHttpParser._
import connectors.mocks.MockPreferenceConnector
import models.Digital

class PreferenceServiceSpec extends MockPreferenceConnector {

  object TestPreferenceService extends PreferenceService(connector)

  "PreferenceService" should {

    "return a Journey model when getJourney is successful" in {

      mockStoreJourneyPreference("testID", Digital)(Right(Success))

      val actualResult = await(TestPreferenceService.storeJourneyPreference("testID", Digital))
      val expectedResult = Right(Success)

      actualResult shouldBe expectedResult
    }

    "return an Error model when getJourney is unsuccessful" in {

      mockStoreJourneyPreference("testID", Digital)(Left(InvalidPreferencePayload))

      val actualResult = await(TestPreferenceService.storeJourneyPreference("testID", Digital))
      val expectedResult = Left(InvalidPreferencePayload)

      actualResult shouldBe expectedResult
    }
  }
}
