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

import org.scalacheck.Gen.{Parameters, const}
import org.scalacheck.{Gen, rng}

case class RichGen[A](g: Gen[A]) extends AnyVal {
  def seeded[IN](in: IN)(implicit tl: ToLong[IN]) =
    g(Parameters.default, rng.Seed(tl.asLong(in)))

  def asMutable[K](implicit en: Enumerable[K]): PersistentGen[K, A] =
    new PersistentGen(
      g, scala.collection.concurrent.TrieMap.empty[K, Option[A]]
    )

  def asMutableWithFilter[K](
                              f: K => Boolean
                            )(implicit en: Enumerable[K]): PersistentGen[K, A] =
    new PersistentGen(
      g,
      scala.collection.concurrent.TrieMap.empty[K, Option[A]],
      f
    )

  def iterator[K](implicit en: Enumerable[K]): Iterator[A] =
    en.iterator.flatMap(seeded(_))

  def optFrequency(i: Int): Gen[Option[A]] =
    Gen.frequency(
      i -> g.map(Some(_)), {
        100 - i
      } -> const[Option[A]](None)
    )

  def hardlyEver: Gen[Option[A]] = optFrequency(1)

  def rarely: Gen[Option[A]] = optFrequency(25)

  def sometimes: Gen[Option[A]] = optFrequency(50)

  def usually: Gen[Option[A]] = optFrequency(75)

  def almostAlways: Gen[Option[A]] = optFrequency(99)

}
