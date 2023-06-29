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
import scala.collection.mutable.{ Map => MMap }

case class PersistentGen[K, V](
                                gen: Gen[V],
                                state: MMap[K, Option[V]],
                                keyFilter: K => Boolean = { _: K => true }
                              )(implicit en: Enumerable[K])
  extends MMap[K, V] {

  def reset(key: K) = {
    state -= key; this
  }

  def +=(kv: (K, V)) = {
    val (k, v) = kv
    state(k) = Some(v)
    this
  }

  def -=(key: K) = {
    state(key) = None
    this
  }

  def get(key: K): Option[V] = {
    state get key
  } match {
    case Some(x) => x
    case None if keyFilter(key) => gen.seeded(key)
    case None => None
  }

  def iterator: Iterator[(K, V)] =
    en.iterator.flatMap { k => get(k).map { v => (k, v) } }

  override def toString = gen.toString

  override def size = {
    en.size - state.values.count(_.isEmpty)
  }.toInt

  override def clear = state.clear()
}
