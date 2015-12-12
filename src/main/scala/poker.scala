import scala.math.Ordering.Implicits._

package object poker {
  import Util._

  sealed abstract class Suit(val symbol: String)

  case object Diamonds extends Suit("♢")
  case object Hearts extends Suit("♡")
  case object Clubs extends Suit("♧")
  case object Spades extends Suit("♤")

  val allSuits: Vector[Suit] = Vector(Diamonds, Hearts, Clubs, Spades)


  // Cards are ranked 2 to 14 from smallest (2) to highest (10, Jack, Queen, King, Ace)
  type CardRank = Int
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

  // Hands are ranked from smallest - a High Card which has a rank of 0,
  // through to highest - a straight flush which has a rank of 8.
  // When constructing a hand rank, a vector of tie-breakers should be provided.
  type HandRank = Int
  case class Rank(handRank: Int)(val tieBreakers: Vector[CardRank]) extends Ordered[Rank] {

    override def compare(otherHand: Rank): Int =
      Ordering[(Int, Vector[CardRank])].compare((handRank, tieBreakers), (otherHand.handRank, otherHand.tieBreakers))

    // A useless string description unless, like me, you
    // regularly forget what the sign of the result of compareTo means.
    def describeWinner(otherHand: Rank): String = compareTo(otherHand) match {
      case x if x > 0 => "I won"
      case 0 => "Tie"
      case _ => "They won"
    }
  }

  // Create a map of the possible hand rank constructors by their numeric rank.
  // To construct a pair of kings with A,8,2 tiebreakers, call:
  // val rank = handsByRank(1)(Vector(14,8,2)
  val handsByRank: Map[Int, Vector[CardRank] => Rank] =
    (0 to 8).map(i => i -> Rank(i) _)(collection.breakOut)

  // Helpers to construct specific ranks. Our earlier pair becomes:
  // val rank = Pair(Vector(14,8,2))
  val HighCard = handsByRank(0)
  val Pair = handsByRank(1)
  val TwoPairs = handsByRank(2)
  val ThreeOfAKind = handsByRank(3)
  val Straight = handsByRank(4)
  val Flush = handsByRank(5)
  val FullHouse = handsByRank(6)
  val FourOfAKind = handsByRank(7)
  val StraightFlush = handsByRank(8)

  // Rank a poker hand.
  //
  // Throws: IllegalArgumentException if a valid poker hand isn't supplied.
  def rankHand(hand: Vector[Card]): Rank = {

    if (hand.size != 5) {
      throw new IllegalArgumentException
    }

    // eg Seq(4D, 6C, 6S, KH, AS) -> Map(1 -> Vector(4,K,A), 2 -> Vector(6))
    val frequencyToCardRanks: Map[Int, Vector[CardRank]] = countCardRanks(hand)

    val values = hand.map(_.rank)
    lazy val highestCard = values.sorted.reverse.take(1)
    lazy val isStraight = values.differences.forall(_ == 1)
    val isFlush = hand.map(_.suit).toSet.size == 1

    if (isStraight && isFlush) {
      StraightFlush(highestCard)
    } else if (frequencyToCardRanks.contains(4)) {
      FourOfAKind(frequencyToCardRanks(4))
    } else if (frequencyToCardRanks.keySet == Set(3, 2)) {
      FullHouse(frequencyToCardRanks(3))
    } else if (isFlush) {
      Flush(highestCard)
    } else if (isStraight) {
      Straight(highestCard)
    } else if (frequencyToCardRanks.contains(3)) {
      ThreeOfAKind(frequencyToCardRanks(3))
    } else if (frequencyToCardRanks.contains(2) && frequencyToCardRanks(2).size == 2) {
      TwoPairs(frequencyToCardRanks(2).sorted.reverse ++ frequencyToCardRanks(1))
    } else if (frequencyToCardRanks.contains(2)) {
      Pair(frequencyToCardRanks(2) ++ frequencyToCardRanks(1).sorted.reverse)
    } else {
      HighCard(values.sorted.reverse)
    }
  }


  private def countCardRanks(hand: Vector[Card]): Map[Int, Vector[CardRank]] = {
    val rankToCards = hand.groupBy(_.rank)
    val rankToCount = rankToCards.mapValues(_.size)
    val countToRankAndCount = rankToCount.groupBy(_._2)
    val countToRanks = countToRankAndCount.mapValues(_.keys.toVector)
    countToRanks
  }

  object Util {
    implicit class VectorWithDifferences[T](l: Vector[T]) {
      def differences(implicit num: Numeric[T]) = l.sliding(2).map(s => s.reduce(num.minus))
    }
  }
}
