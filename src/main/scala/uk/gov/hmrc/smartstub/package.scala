/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc

import org.scalacheck.Gen

import scala.language.implicitConversions
import cats.{Invariant, Monad}

package object smartstub
  extends Enumerable{

  implicit def genToRich[A](g: Gen[A]): RichGen[A] = RichGen(g)

  implicit def enumToGen[A](e: Enumerable[A]): Gen[A] = e.gen

  implicit class AdvGen(
    val g: Gen.type
  ) extends AnyVal with Loader
      with Names
      with Addresses
      with Temporal
      with Pattern
      with Companies
  {
    def boolean: Gen[Boolean] = Gen.oneOf(true, false)

    def age: Gen[Int] = Gen.frequency(
      80 → Gen.choose(0,65),
      15 → Gen.choose(66,80),
      5  → Gen.choose(81,101)
    )
  }

  implicit val longEnum: Enumerable[Long] = Enumerable.instances.longEnum

  implicit class PatternContext(val sc: StringContext) extends AnyVal {
    def pattern(i: Any*): Enumerable[String] = Gen.pattern(
      sc.parts.head.map {
        case d if d.isDigit => '0' to d
        case u if u.isUpper => 'A' to u
        case l if l.isLower => 'a' to l
        case x => Seq(x)
      }).imap(_.mkString)(s => {s: Seq[Char]})
  }

  implicit val enumInvariant: Invariant[Enumerable] = new Invariant[Enumerable] {
    def imap[A, B](fa: Enumerable[A])(f: A => B)(finv: B => A): Enumerable[B] =
      fa.imap(f)(finv)
  }

  def repeatM[M[_],A](input: M[A], f: A => M[A], n: Int)(implicit monad: Monad[M]): M[A] = {

    /**
      * inner method is used to avoid possible overhead of implicit
      * resolution on recursive call
      */
    @annotation.tailrec
    def inner(input: M[A], n: Int): M[A] =
      n match {
        case 0 => input
        case n => inner(monad.flatMap(input)(f), n - 1)
      }
    inner(input, n)
  }
}
