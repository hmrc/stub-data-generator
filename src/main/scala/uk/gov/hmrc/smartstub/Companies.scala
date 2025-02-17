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

trait Companies extends Any {
  def company: Gen[String] = Companies.company
}

object Companies extends Loader {

  val markovChain = {
    val source = scala.io.Source.fromURL(getClass.getResource("company-names.txt"))

    try {
      new MarkovChain[Char](
        data = source.mkString.toList,
        windowSize = 3,
        terminus = " ".toList)
    } finally {
      source.close()
    }
  }

  def company: Gen[String] = for {
    size <- Gen.choose(5, 20)
    base <- companyBase(size)
    suffix <- Gen.oneOf("Ltd.,Group,Inc.,Plc.,Holdings".split(",")).sometimes
  } yield {
    List(Some(base), suffix).flatten.mkString(" ")
  }

  private def companyBase(length: Int): Gen[String] =
    markovChain.sized(length).map { x => removeShortWordsAndCapitalise(x.mkString) } 

  private def removeShortWordsAndCapitalise(companyName: String, minLength: Int = 2): String = {
    companyName.split(" ").filter(_.length > minLength).map(_.capitalize).mkString(" ")
  }
}
