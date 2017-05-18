import shapeless._
import shapeless.labelled._
import org.scalacheck._
import hmrc.smartstub._

object AutoGen {

  def apply[A](implicit e: Gen[A]): Gen[A] = e

  trait GenProvider[A] { def gen(str: String): Gen[A] }

  implicit def strProvider: GenProvider[String] = new GenProvider[String] {
    override def gen(name: String): Gen[String] = name match {
      case "forename" => Gen.forename
      case "surname" => Gen.surname
      case _ => Gen.alphaStr
    }
  }

  // // Don't think I need this 
  // implicit def hnilProvider: GenProvider[HNil] = new GenProvider[HNil] {
  //   override def gen(name: String): Gen[HNil] = Gen.const(HNil)
  // }

  implicit def hnilGen: Gen[HNil] = Gen.const(HNil)

  implicit def hconsGen[S <: Symbol, H, T <: HList]( implicit
    witness: Witness.Aux[S],
    headProvider: GenProvider[H],
    tailGen: Gen[T]
  ): Gen[FieldType[S,H] :: T] = for {
    head <- headProvider.gen(witness.value.name)
    tail <- tailGen
  } yield { field[S](head) :: tail }


  implicit def genGeneric[A, R <: HList](
    implicit gen: LabelledGeneric.Aux[A, R],
    enc: Gen[R]
  ): Gen[A] = enc.map{gen.from}

}
