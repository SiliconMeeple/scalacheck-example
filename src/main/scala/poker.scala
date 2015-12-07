import scala.math.Ordering.Implicits._

package object poker {

  object Util {
    def Desc[T : Ordering] = implicitly[Ordering[T]].reverse

    implicit class VectorWithDifferences[T](l: Vector[T]) {
      def differences(implicit num: Numeric[T]) = l.sliding(2).map(s =>  num.minus(s.head, s(1)))
    }
  }

  import Util._

  type CardRank = Int
  type HandRank = Int

  sealed abstract class Suit(val symbol: String)

  case object Diamonds extends Suit("♢")
  case object Hearts extends Suit("♡")
  case object Clubs extends Suit("♧")
  case object Spades extends Suit("♤")

  val allSuits: Vector[Suit] = Vector(Diamonds, Hearts, Clubs, Spades)
  val allCardRanks: Vector[CardRank] = (2 to 14).toVector

  case class Card(suit: Suit, rank: CardRank) {
    require(rank >= 2 && rank <= 14)

    override lazy val toString = (rank match {
      case 11 => "J"
      case 12 => "Q"
      case 13 => "K"
      case 14 => "A"
      case x => x.toString
    }) + suit.symbol
  }

  case class Rank(handRank: Int)(val tieBreakers: Vector[CardRank]) extends Ordered[Rank] {

    override def compare(otherHand: Rank): Int = ???

    def describeWinner(otherHand: Rank): String = compareTo(otherHand) match {
      case x if x > 0 => "I won"
      case 0 => "Tie"
      case _ => "They won"
    }
  }

  val handsByRank: Map[Int, Vector[CardRank] => Rank] =
    (0 to 8).map(i => i -> Rank(i) _)(collection.breakOut)

  val HighCard = handsByRank(0)
  val Pair = handsByRank(1)
  val TwoPairs = handsByRank(2)
  val ThreeOfAKind = handsByRank(3)
  val Straight = handsByRank(4)
  val Flush = handsByRank(5)
  val FullHouse = handsByRank(6)
  val FourOfAKind = handsByRank(7)
  val StraightFlush = handsByRank(8)

  def rankHand(hand: Vector[Card]): Rank = ???

}
