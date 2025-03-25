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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalacheck.Gen

class MarkovChainSpec extends AnyFlatSpec with Matchers {

  "A MarkovChain[String]" should "generate a non-empty sequence of strings" in {
    val chain = new MarkovChain(Seq("one", "two", "three", "two"), windowSize = 2)
    val result = chain.sized(5).sample
    result shouldBe defined
    result.get.length should be <= 5
    result.get.foreach(s => Seq("one", "two", "three").contains(s) shouldBe true)
  }

  it should "return None when given an invalid seed" in {
    val chain = new MarkovChain(Seq("a", "b", "c"), windowSize = 2)
    val result = chain.next(Seq("x", "y")).sample
    result shouldBe defined
    result.get shouldBe None
  }

  it should "generate values matching original data when seed is valid" in {
    val data = Seq("apple", "banana", "apple", "cherry")
    val chain = new MarkovChain(data, windowSize = 1)
    val result = chain.next(Seq("apple")).sample
    result shouldBe defined
    result.get should not be None
    result.get.get should (equal("banana") or equal("cherry"))
  }

  it should "generate empty sequence if next returns None initially" in {
    val data = Seq("a", "b", "c")
    val invalidSeed = Seq("x", "y") // This seed does not exist in the training data
    val chain = new MarkovChain(data, windowSize = 2, terminus = invalidSeed)

    val result = chain.sized(5).sample
    result shouldBe defined
    result.get shouldBe empty
  }


  it should "throw IllegalArgumentException for invalid inputs" in {
    an [IllegalArgumentException] should be thrownBy new MarkovChain(Seq(), 1)
    an [IllegalArgumentException] should be thrownBy new MarkovChain(Seq("a"), 2)
    an [IllegalArgumentException] should be thrownBy new MarkovChain(Seq("a"), 0)
  }
}
