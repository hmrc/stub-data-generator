package hmrc.smartstub.enumerable

import org.scalatest.prop.Checkers
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._
import org.scalatest.FlatSpec

import instances._
import Enumerable.ops._

class SetSpec extends FlatSpec with Checkers {

  "A Nino" should "convert back and from a Long unchanged" in {
    check{(a: Long) =>
      val t = a.abs % 17576000000L
      t == Enumerable[Nino].to(t).from}
  }
}
