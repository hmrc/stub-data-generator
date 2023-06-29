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

trait Pattern extends Any {

  def pattern[T](pattern: Seq[Seq[T]]): Enumerable[Seq[T]] = new Enumerable[Seq[T]] {

    private val chars = pattern.reverse.map(_.zipWithIndex.toMap.view.mapValues(_.toLong).toMap)
    private val charsR = chars.map(_.map(_.swap))

    private val charPermutations = chars.map {
      _.size.toLong
    }
    val size: Long = charPermutations.product

    private def maxValue = size - 1

    private val charValues = charPermutations.tails.map {
      _.product
    }.toList.tail

    private def zip3[A, B, C](as: Iterable[A], bs: Iterable[B], cs: Iterable[C]): Iterable[(A, B, C)] =
      as.zip(bs).zip(cs).map {
        case ((a, b), c) => (a, b, c)
      }

    def asLong(i: Seq[T]): Long =
      zip3(i.reverse, charValues, chars).map {
        case (c, value, cMap) => cMap(c) * value
      }.sum

    override def get(i: Long): Option[Seq[T]] = i match {
      case low if low < 0 => None
      case high if high > maxValue => None
      case _ => charsR.zip(charValues).foldLeft {
        (i, List.empty[T])
      } {
        case ((r, vals), (posChars, posValue)) =>
          (r % posValue, posChars(r / posValue) :: vals)
      } match {
        case (0, x) => Some(x)
        case (r, f) => throw new IllegalStateException(s"Remainder $r with generated $f")
      }
    }
  }
}
