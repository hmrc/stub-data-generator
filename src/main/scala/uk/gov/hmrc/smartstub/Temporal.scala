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

import org.scalacheck.Gen
import org.scalacheck.Gen.choose

import java.time.LocalDate

trait Temporal extends Any {
  def date(start: LocalDate, end: LocalDate): Gen[LocalDate] =
    choose(start.toEpochDay, end.toEpochDay).map(LocalDate.ofEpochDay)

  def date: Gen[LocalDate] = date(1970, 2000)

  def date(
            start: Int,
            end: Int
          ): Gen[LocalDate] = date(
    LocalDate.of(start, 1, 1),
    LocalDate.of(end, 12, 31)
  )
}
