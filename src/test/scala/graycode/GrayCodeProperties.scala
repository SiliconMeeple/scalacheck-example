package graycode

import org.scalacheck._
import Prop.forAll
import graycode.GrayCode._

object GrayCodeProperties extends Properties("Binary reflected gray code") {
  val numbersLessThan128 = Gen.posNum[Int].suchThat(_ < 128)

  property("") = forAll(numbersLessThan128) {
    forAll(numbersLessThan128) { (x: Int) =>
      false
    }
  }

}
