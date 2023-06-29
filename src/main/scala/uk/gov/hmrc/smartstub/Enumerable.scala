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

import org.scalacheck._
import simulacrum._

//import scala.language.implicitConversions

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

  def imap[B](f: A => B)(invf: B => A): Enumerable[B] = {
    val base = this
    new Enumerable[B] {
      override def get(i: Long) = base.get(i).map(f)
      def size: Long = base.size
      def asLong(i: B): Long = base.asLong(invf(i))
    }
  }
}

object Enumerable {
  object instances {
    implicit val longEnum: Enumerable[Long] = new Enumerable[Long] {
      override def get(i: Long): Option[Long] = Some(i).filter{_ >= 0}
      def asLong(i: Long): Long = i
      val size: Long = Long.MaxValue
    }

    implicit val ninoEnum: Enumerable[String] = pattern"ZZ 99 99 99 D"
    implicit val ninoEnumNoSpaces: Enumerable[String] = pattern"ZZ999999D"

    implicit val utrEnum: Enumerable[String] = {
      def checkDigit(in: String): Char = {
        val checkString = "21987654321"
        val weights = List(6, 7, 8, 9, 10, 5, 4, 3, 2)
        val pos = {weights zip in}.map {
          case (weight, char) => weight * char.asDigit
        }.sum % checkString.length
        checkString(pos)
      }

      pattern"999999999".imap { in =>
        checkDigit(in).toString ++ in
      } {
        case s if s.head == checkDigit(s.tail) => s.tail
        case _ => throw new IllegalArgumentException
      }
    }

    implicit val sortCodeEnum: Enumerable[String] = pattern"99-99-99"
    implicit val accountNumberEnum: Enumerable[String] = pattern"99999999"

    implicit val empRefEnum: Enumerable[String] = pattern"999/Z999"
  }
}
