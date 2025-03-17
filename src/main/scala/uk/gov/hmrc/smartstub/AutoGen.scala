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

package uk.gov.hmrc.smartstub

import org.scalacheck.*

import java.time.LocalDate
import scala.compiletime.constValue
import org.scalacheck.Gen
import scala.compiletime.constValue
import scala.deriving.Mirror
import shapeless3.deriving.K0.Generic._
import scala.reflect.Selectable.reflectiveSelectable


object AutoGen extends LowPriorityGenProviderInstances {

  trait GenProvider[A] {
    val gen: Gen[A]
  }

  def apply[A](implicit e: GenProvider[A]): Gen[A] = e.gen

  // instance constructor
  def instance[A](f: Gen[A]): GenProvider[A] = new GenProvider[A] {
    override val gen: Gen[A] = f
  }

  object GenProvider {
    def apply[A](implicit gp: GenProvider[A]): Gen[A] = gp.gen

    def instance[A](f: Gen[A]): GenProvider[A] = new GenProvider[A] {
      override val gen: Gen[A] = f
    }
  }

  trait TupleSize[T <: Tuple] {
    def size: Int
  }

  object TupleSize {
    given empty: TupleSize[EmptyTuple] with {
      def size: Int = 0
    }

    given nonEmpty[H, T <: Tuple](using tail: TupleSize[T]): TupleSize[H *: T] with {
      def size: Int = 1 + tail.size
    }
  }

  // Define SumGenProvider for a tuple T (the alternatives for a sealed trait)
  trait SumGenProvider[T <: Tuple] {
    val gen: Gen[Int]
  }

  object SumGenProvider {
    def apply[T <: Tuple](using sg: SumGenProvider[T]): SumGenProvider[T] = sg

    // Provide an instance that uses the TupleSize to determine the number of alternatives.
    given [T <: Tuple](using ts: TupleSize[T]): SumGenProvider[T] with {
      override val gen: Gen[Int] = Gen.choose(0, ts.size - 1)
    }
  }

  implicit def providerUnnamed[A](implicit g: GenProvider[A]): String => GenProvider[A] = _ => g

  // Named types
  implicit def providerSeqNamed[A](s: String)(implicit inner: => String => GenProvider[A]): GenProvider[Seq[A]] =
    instance(Gen.listOf(inner(s).gen))

  implicit def providerSetNamed[A](s: String)(implicit inner: => String => GenProvider[A]): GenProvider[Set[A]] =
    instance(Gen.listOf(inner(s).gen).map(_.toSet))

  implicit def providerVectorNamed[A](s: String)(implicit inner: => String => GenProvider[A]): GenProvider[Vector[A]] =
    instance(Gen.listOf(inner(s).gen).map(l =>l.toVector))

  implicit def providerOptionNamed[A](s: String)(implicit inner: => String => GenProvider[A]): GenProvider[Option[A]] =
    instance(Gen.option(inner(s).gen))

  implicit def providerIntNamed: String => GenProvider[Int] = s => instance ({
    s.toLowerCase match {
      case "age" => Gen.age
      case _     => Gen.choose(1, 1000)
    }
  })

  implicit def providerStringNamed: String => GenProvider[String] = s => instance ({
    s.toLowerCase match {
      case "forename" | "firstname" => Gen.forename()
      case "surname" | "lastname" | "familyname" => Gen.surname
      case x if x.toLowerCase.contains("address") =>
        Gen.ukAddress.map{_.mkString(", ")}
      case "gender" | "sex" => Gen.oneOf("male", "female")
      case "nino" => Enumerable.instances.ninoEnum.gen
      case "utr" => Enumerable.instances.utrEnum.gen
      case "company" => Gen.company
      case _ => Gen.alphaStr
    }
  })

  implicit def providerLocalDate: String => GenProvider[LocalDate] = s => instance({
    s.toLowerCase match {
      case "dateofbirth" | "dob" | "birthdate" | "bornon" | "birthday" =>
        // the date below is hard coded to keep the date's generated consistent with time -
        // this implies there will never be a date of birth generated after the hard coded
        // date
        Gen.age.map(a => LocalDate.of(2017,9,1).minusYears(a.toLong))
      case _                                                           => Gen.date
    }
  })

  implicit def providerBooleanNamed: String => GenProvider[Boolean] =
    _ => instance(Gen.oneOf(true,false))

  // generic instance
  implicit def providerGeneric[A]
  (implicit
   generic: Mirror.ProductOf[A],
   hGenProvider: => GenProvider[generic.MirroredElemTypes]
  ): GenProvider[A] =
    instance(hGenProvider.gen.map(t => generic.fromProduct(t)))

  implicit def providerSum[A]
  (implicit
   s: Mirror.SumOf[A],
   sumProvider: SumGenProvider[s.MirroredElemTypes]
  ): GenProvider[A] =
    GenProvider.instance(
    sumProvider.gen.map { i =>
      s.asInstanceOf[{ def fromOrdinal(i: Int): A }].fromOrdinal(i)
    }
  )

  // HList instances
  type FieldType[K, V] = V

  implicit def genProviderByFieldName[H](implicit gp: GenProvider[H]): String => GenProvider[H] =
    (_: String) => gp

  implicit val providerHNil: GenProvider[EmptyTuple] = instance(Gen.const(EmptyTuple))

  implicit def providerHCons[K <: String & Singleton, H, T <: Tuple]
  (implicit
   valueOfK: ValueOf[K],
   hGenProvider: String => GenProvider[H],
   tGenProvider: => GenProvider[T]
  ): GenProvider[H *: T] = {
    val fieldName: String = valueOfK.value
    val headGenerator = hGenProvider(fieldName).gen.flatMap(f =>
      tGenProvider.gen.map { t =>
        f *: t
      }
    )
    instance(headGenerator)
  }

  // Coproduct instances
  override implicit def providerCCons[K <: String & Singleton, H, T <: Tuple, L <: Int]
  (implicit
   valueOfK: ValueOf[K],
   valueOfL: ValueOf[L],
   hGenProvider: String => GenProvider[H],
   tGenProvider: => GenProvider[T],
  ): GenProvider[H *: T] = {

    val fieldName = valueOfK.value
    val length = valueOfL.value

    val headGenerator = hGenProvider(fieldName).gen.flatMap { h =>
      tGenProvider.gen.map(t => h *: t)
    }

    val tailGenerator: Gen[H *: T] = tGenProvider.gen.map {
      case t: Tuple => t.asInstanceOf[H *: T]
    }

    if (length == 1) {
      instance(headGenerator)
    } else {
      instance(Gen.oneOf(tailGenerator, headGenerator))
    }
  }


}

trait LowPriorityGenProviderInstances {

  import AutoGen.{GenProvider, instance}

  implicit def providerUnnamed2[A](implicit g: String => GenProvider[A]): GenProvider[A] = g("")

  implicit def providerHCons2[K <: Symbol, H, T <: Tuple, L <: Int]
  (implicit
   valueOfK: ValueOf[K],
   valueOfL: ValueOf[L],
   hGenProvider: => GenProvider[H],
   tGenProvider: => GenProvider[T]
  ): GenProvider[H *: T] = instance(
    hGenProvider.gen.flatMap(f =>
      tGenProvider.gen.map { t =>
        f *: t
      }
    )
  )

  implicit def providerCCons[K <: String & Singleton, H, T <: Tuple, L <: Int]
  (implicit
   valueOfK: ValueOf[K],
   valueOfL: ValueOf[L],
   hGenProvider: String => GenProvider[H],
   tGenProvider: => GenProvider[T],
  ): GenProvider[H *: T] = {

    val fieldName = valueOfK.value
    
    val headGenerator = hGenProvider(fieldName).gen.flatMap { h =>
      tGenProvider.gen.map { t =>
        h *: t
      }
    }

    val tailGenerator: Gen[H *: T] = tGenProvider.gen.map {
      case t: Tuple => t.asInstanceOf[H *: T]
    }
    val length = valueOfL.value

    if(length == 1){
      instance(headGenerator)
    } else {
      instance(Gen.oneOf(tailGenerator, headGenerator))
    }
  }
}

