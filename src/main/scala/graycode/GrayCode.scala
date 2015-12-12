package graycode

object GrayCode {
  def encode(x: Int): String = (x ^ (x >>> 1)).toBinaryString

  def decode(x: String): Int = Integer.parseInt(x.scanLeft(0)(_ ^ _.asDigit).tail.mkString, 2)
}

object Main extends App {
  (1 to 8).foreach(i => println(s"| $i | ${i.toBinaryString} | ${GrayCode.encode(i)} |"))
}
