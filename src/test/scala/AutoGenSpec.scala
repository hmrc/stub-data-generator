package hmrc.smartstub

import org.scalatest.prop.Checkers
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._
import org.scalatest._
import org.scalacheck._

class AutoGenSpec extends FlatSpec with Checkers with Matchers {

  import AutoGen._

  "The AutoGen" should "derive a Gen for a HList" in {
    import shapeless.labelled._    
    import shapeless._
    val x = 'forename
    val y = 'surname
    "val gen = AutoGen[FieldType[x.type,String] :: HNil]" should compile
  }


  it should "derive a Gen[A] for a case class" in {
    case class Blah(forename: String)
    "val gen: Gen[Blah] = AutoGen[Blah]" should compile
  }
}
