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

//
//package mocks
//
//import config.AppConfig
//import config.features.Features
//import javax.inject.Inject
//import org.scalamock.scalatest.MockFactory
//import play.api.{Configuration, Environment}
//
//class MockAppConfig @Inject()(implicit override val runModeConfiguration: Configuration, environment: Environment)
//  extends AppConfig with MockFactory {
//
//  lazy val mockFeatures: Features = mock[Features]
//
//  def progressiveDisclosureEnabled(enabled: Boolean): Unit = mockFeatures.progressiveDisclosureEnabled(enabled)
//
//  override val features = mockFeatures
//
//}