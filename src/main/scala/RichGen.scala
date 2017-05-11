package hmrc.smartstub

import org.scalacheck._
import Gen._

case class RichGen[A](g: Gen[A]) extends AnyVal {
  def seeded[IN](in: IN)(implicit tl: ToLong[IN]) =
    g(Parameters.default, rng.Seed(tl.asLong(in)))

  def asMutable[K](implicit en: Enumerable[K]): PersistentGen[K,A] =
    new PersistentGen(
      g, scala.collection.concurrent.TrieMap.empty[K,Option[A]]
    )

  def iterator[K](implicit en: Enumerable[K]): Iterator[A] =
    en.iterator.flatMap(seeded(_))
}
