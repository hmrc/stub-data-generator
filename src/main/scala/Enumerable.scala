package hmrc.smartstub

import scala.language.implicitConversions

import simulacrum._
import org.scalacheck._

@typeclass trait FromLong[A] {
  def size: Long  
  def get(i: Long): Option[A] = i match {
    case low if low < 0 => None
    case high if high > size - 1 => None
    case _ => Some(apply(i))
  }
  
  def apply(i: Long): A = get(i).getOrElse {
    throw new IndexOutOfBoundsException
  }

  def gen: Gen[A] = Gen.choose(0, size - 1).map{apply(_)}
  def arbitrary: Arbitrary[A] = Arbitrary { gen }
}

@typeclass trait ToLong[A] {
  def asLong(i: A): Long
}

@typeclass trait Enumerable[A] extends FromLong[A] with ToLong[A] {

  def head = apply(0L)
  def last = apply(size - 1)

  def succ(i: A): A = apply(asLong(i) + 1)
  def pred(i: A): A = apply(asLong(i) - 1)

  def iterator: Iterator[A] = { 
    val s = size - 1
    new Iterator[A] {
      var pos: A = head
      def hasNext: Boolean = asLong(pos) < { s - 1}
      def next(): A = {pos = succ(pos); pos}
    }
  }
}

object Enumerable {
  def patterned(pattern: String): Enumerable[String] = patterned(
    pattern.map {
      case d if d.isDigit => '0' to d
      case u if u.isUpper => 'A' to u
      case l if l.isLower => 'a' to l        
      case x => Seq(x)
    })

  def patterned(pattern: Seq[Seq[Char]]) = new Enumerable[String] {

    private val chars = pattern.reverse.map(_.zipWithIndex.toMap.mapValues{_.toLong})
    private val charsR = chars.map{_.map{_.swap}}
    private val charPermutations = chars.map{_.size.toLong}
    val size = charPermutations.product
    private def maxValue = size - 1
    private val charValues = charPermutations.tails.map{_.product}.toList.tail

    private def zip3[A,B,C](as: Iterable[A], bs: Iterable[B], cs: Iterable[C]): Iterable[(A,B,C)] =
      as.zip(bs).zip(cs).map {
        case ((a,b),c) => (a,b,c)
      }

    def asLong(i: String): Long =
      zip3(i.reverse, charValues, chars).map {
        case (c, value, cMap) => cMap(c) * value
      }.sum

    override def get(i: Long): Option[String] = i match {
      case low if low < 0 => None
      case high if high > maxValue => None
      case _ => charsR.zip(charValues).foldLeft{(i, List.empty[Char])} {
        case ((r, vals), (posChars,posValue)) =>
          (r % posValue, posChars(r / posValue) :: vals)
      } match {
        case (0,x) => Some(x.mkString)
        case (r,f) => throw new IllegalStateException(s"Remainder $r with generated $f")
      }
    }      
  }

  object instances {
    implicit val longEnum = new Enumerable[Long] {
      override def get(i: Long): Option[Long] = Some(i).filter{_ >= 0}
      def asLong(i: Long): Long = i
      val size = Long.MaxValue
    }

    type Nino = String
    implicit val ninoEnum: Enumerable[Nino] = Enumerable.patterned("ZZ 99 99 99 Z")

    type Utr = String
    implicit val utrEnum: Enumerable[Utr] = Enumerable.patterned("99999 99999")

    type EmployerReference = String
    implicit val empRefEnum: Enumerable[EmployerReference] = Enumerable.patterned("999/Z999")
  }
}
