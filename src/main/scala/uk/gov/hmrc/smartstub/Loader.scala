/*
 * Copyright 2025 HM Revenue & Customs
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
import org.scalacheck.Gen.{const, frequency, oneOf}

trait Loader extends Any {
  def loadWeightedFile(file: String): Gen[String] = {
    val resource = this.getClass.getResource(file)
    val source = scala.io.Source.fromURL(resource)

    try {
      val data = source.getLines()
      val nocomments = data.filterNot(_.startsWith("#"))
      val freqTuples = nocomments.map(_.split("\t").toList).collect {
        case (f :: w :: _) => (w.collect { case c if c.isDigit => c }.toInt, const(f))
      }.toSeq
      frequency(freqTuples: _*)
    } finally {
      source.close
    }
  }

  def loadFile(file: String): Gen[String] = {
    val resource = this.getClass.getResource(file)
    val source = scala.io.Source.fromURL(resource)
    try {
      val data = scala.io.Source.fromURL(resource).getLines()
      oneOf(data.filterNot(_.startsWith("#")).toList)
    } finally {
      source.close()
    }
  }

}
