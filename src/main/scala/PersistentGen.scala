package hmrc.smartstub

import org.scalacheck._
import scala.collection.mutable.{Map => MMap, AbstractMap}

class PersistentGen[K, V](gen: Gen[V], state: MMap[K,Option[V]])(implicit en: Enumerable[K]) extends AbstractMap[K,V] {

  def reset (key: K) = { state -= key; this }

  def +=(kv: (K, V)) = {
    val (k,v) = kv
    state(k) = Some(v)
    this
  }

  def -=(key: K) = {
    state(key) = None
    this
  }

  def get(key: K): Option[V] = {state get key} match {
    case Some(x) => x
    case None => gen.seeded(key)
  }

  def iterator: Iterator[(K,V)] =
    en.iterator.flatMap{k => get(k).map{v => (k, v)}}

  override def toString = gen.toString

} 
