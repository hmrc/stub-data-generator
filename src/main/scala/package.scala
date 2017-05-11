package hmrc

import org.scalacheck._

import scala.language.implicitConversions

package object smartstub extends Enumerable.ToEnumerableOps {
  implicit def genToRich[A](g: Gen[A]): RichGen[A] = RichGen(g)

  implicit class AdvGen(
    val g: Gen.type
  ) extends AnyVal with Loader
      with Names
      with Addresses
      with Temporal

  implicit val longEnum = Enumerable.instances.longEnum
}
