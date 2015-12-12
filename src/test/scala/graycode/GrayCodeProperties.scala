package graycode

import org.scalacheck.Gen._
import org.scalacheck._
import Prop.forAll
import graycode.GrayCode._
import org.scalatest.{EitherValues, Matchers, FlatSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

object GrayCodeProperties extends Properties("Binary reflected gray code") {
  val numbersLessThan128 = Gen.posNum[Int].suchThat(_ < 128)

  property("be reversible back to the same number") = forAll(numbersLessThan128) { (x: Int) =>
    decode(encode(x)) == x
  }


  property("be a binary string") = forAll(numbersLessThan128) { (x: Int) =>
    Set('0', '1').union(encode(x).toSet) == Set('0', '1')
  }

  property("not change more than a single bit when going up one number") =
    forAll(numbersLessThan128) { (x: Int) =>
      hammingDistance(encode(x), encode(x + 1)) <= 1
    }

  property("be binary reflected") = forAll(numbersLessThan128) { (x: Int) =>
    encode(x).zip(x.toBinaryString).sliding(2).forall {
      case Seq((g0, b0), (g1, b1)) =>
        if (b0 == '1') {
          g1 == b1
        } else {
          g1 == b1
        }
      case _ => true
    }
  }

  def hammingDistance(s1: String, s2: String): Int = s1.zip(s2).count(c => c._1 != c._2)
}


