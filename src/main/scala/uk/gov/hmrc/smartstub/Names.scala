/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.smartstub

import org.scalacheck.Gen._
import org.scalacheck._

sealed trait Gender
case object Male extends Gender
case object Female extends Gender

trait Names extends Any {
  def surname = Names.surname
  def _forenames = Names._forenames

  def gender: Gen[Gender] = oneOf(Male, Female)
  def forename(): Gen[String] = for {
    g <- gender
    v <- _forenames(g)
  } yield (v)
  def forename(g: Gender): Gen[String] = _forenames(g)

}

object Names extends Loader {
  lazy val surname: Gen[String] = loadWeightedFile("surnames.txt")
  lazy val _forenames: Map[Gender, Gen[String]] = Map(
    ( Male -> loadFile("forenames-male.txt")),
    ( Female -> loadFile("forenames-female.txt"))
  )
}
