package hmrc

import org.scalacheck._

import scala.language.implicitConversions

package object smartstub
    extends Enumerable.ToEnumerableOps
    with ToLong.ToToLongOps
    with FromLong.ToFromLongOps
{
  implicit def genToRich[A](g: Gen[A]): RichGen[A] = RichGen(g)

  implicit class AdvGen(
    val g: Gen.type
  ) extends AnyVal with Loader
      with Names
      with Addresses
      with Temporal
      with Pattern

  implicit val longEnum = Enumerable.instances.longEnum

  implicit class PatternContext(val sc: StringContext) extends AnyVal {
    def pattern(i: Any*): Enumerable[String] = Gen.pattern(
      sc.parts.head.map {
        case d if d.isDigit => '0' to d
        case u if u.isUpper => 'A' to u
        case l if l.isLower => 'a' to l
        case x => Seq(x)
      }).map(_.mkString, s => {s: Seq[Char]})
  }
}
