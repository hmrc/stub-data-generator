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
import org.scalacheck.Gen._

/*
 * A MarkovChain implementation that will return a plausible/probable
 * next item given a seed sub-sequence from the original data. Ideally
 * the seed should be windowSize length but it will return something
 * based on any seed (including empty) as long as the non-empty seed
 * exists in the original data.
 *
 * If a terminus is supplied it will use this as seed whenever an
 * empty seed is supplied.
 */
class MarkovChain[A](
                      val data: Seq[A],
                      val windowSize: Int,
                      val terminus: Seq[A] = Nil
                    ) {
  //validate data and windowSize on construction.
  require(windowSize > 0, "windowSize must be greater than 0")
  require(data.nonEmpty, "Input data must not be empty")
  require(data.length > windowSize, "Input data must be longer than window size")

  private val multimap: Map[Int, Map[Seq[A], Gen[A]]] =
    (1 to windowSize).map(i => i -> markov(i)).toMap

  //In cases where terminus is empty and data is large, accessing start eagerly can be inefficient.Prevents unnecessary computation.
  private lazy val start: Seq[A] = terminus.nonEmpty match {
    case true  => terminus
    case false => multimap(1).keySet.head
  }

  def sized(numElems: Int): Gen[Vector[A]] = {
    // Helper function to recursively generate elements
    def loop(remaining: Int,  acc: Vector[A], lastOpt:Option[A]): Gen[Vector[A]]= {
      if (remaining <= 0 ) Gen.const(acc)
      else lastOpt match {
        case None => Gen.const(acc) // No more elements can be generated
        case Some(_) =>
          next(trimSeed(acc)).flatMap {
            case Some(nextVal) => loop(remaining - 1, acc :+ nextVal, Some(nextVal))
            case None => Gen.const(acc)  // Chain terminated
          }
      }
    }

    // Start the generation process
    next().flatMap {
      case Some(firstVal) => loop(numElems - 1 , Vector(firstVal), Some(firstVal))
      case None => Gen.const(Vector.empty[A])
    }
  }

  private def markov(ws: Int): Map[Seq[A], Gen[A]] = {
    val subsequences: List[Seq[A]] =
      data.view.iterator.sliding(ws + 1).withPartial(false).toList

    val kvsets: Map[Seq[A], List[A]] = subsequences
      .map(x => (x.init, x.last))
      .groupBy(_._1)
      .map { case (k, v) => k -> v.map(_._2) }

    kvsets.map { case (k, vList) =>
      val table = vList.groupBy(identity).view.mapValues(_.size).toMap.map {
        case (a, count) => count -> const(a)
      }
      k -> frequency(table.toSeq: _*)
    }
  }

  private def trimSeed(seed: Seq[A]): Seq[A] =
    if (seed.length > windowSize) seed.takeRight(windowSize) else seed

  def next(seed: Seq[A] = start): Gen[Option[A]] = {
    val last = trimSeed(seed)
    val genOpt = multimap.get(last.size).flatMap(_.get(last))
    genOpt match {
      case Some(gen) => gen.map(Some(_))
      case None => Gen.const(None)
    }
  }

}