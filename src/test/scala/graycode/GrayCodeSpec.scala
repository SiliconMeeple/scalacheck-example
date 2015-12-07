package graycode

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import graycode.GrayCode._

class GrayCodeSpec extends FlatSpec with GeneratorDrivenPropertyChecks with Matchers  {

  behavior of "binary reflected Gray code"

  val numbersLessThan128 = Gen.posNum[Int].suchThat(_ < 128)

  it should "" in {
    forAll(numbersLessThan128) { (x: Int) =>
      fail
    }
  }

}
