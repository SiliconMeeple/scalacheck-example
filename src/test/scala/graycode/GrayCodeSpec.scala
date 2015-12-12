package graycode

import org.scalacheck.Gen
import org.scalacheck.Gen._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{EitherValues, FlatSpec, Matchers}
import graycode.GrayCode._

class GrayCodeSpec extends FlatSpec with GeneratorDrivenPropertyChecks with Matchers with EitherValues {

  import GrayCode._

  behavior of "binary reflected Gray code"

  val numbersLessThan128 = posNum[Int].suchThat(_ < 128)

  it should "be reversible back to the same number" in {
    forAll(numbersLessThan128) { (x: Int) =>
      decode(encode(x)) shouldBe x
    }
  }

  it should "be a binary string" in {
    forAll(numbersLessThan128) { (x: Int) =>
      Set('0', '1').union(encode(x).toSet) shouldBe Set('0', '1')
    }
  }

  it should "not change more than a single bit when going up one number" in {
    forAll(numbersLessThan128) { (x: Int) =>
      withClue((encode(x), encode(x + 1))) {
        hammingDistance(encode(x), encode(x + 1)) should be <= 1
      }
    }
  }

  it should "be binary reflected" in {
    forAll(numbersLessThan128) { (x: Int) =>
      encode(x).zip(x.toBinaryString).sliding(2).foreach {
        case Seq((g0, b0), (g1, b1)) =>
          if (b0 == '1') {
            g1 should not be b1
          } else {
            g1 shouldBe b1
          }
        case _ =>
      }
    }
  }

  def hammingDistance(s1: String, s2: String): Int = s1.zip(s2).count(c => c._1 != c._2)
}
