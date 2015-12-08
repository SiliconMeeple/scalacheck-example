package poker

import org.scalacheck.Gen.posNum
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck.Properties
import poker.PokerGenerators._

import scala.math.Ordering.Implicits._
import scala.util.Try

object PokerHandProperties extends Properties("Poker hand ranking") {

  property("a straight flush") =
    forAll(topCardInAStraight, suits) { (highCard, suit) =>
      val descendingCards = Stream.from(highCard, -1).map { value => Card(suit, value) }
      rankHand(descendingCards.take(5).toVector) == StraightFlush(Vector(highCard))
    }

  property("four of a kind") =
    forAll(twoDifferentCardRanks) { (ranks) =>
      val (rank1, rank2) = ranks
      val fourOfAKind = allSuits.zip(Vector.fill(4)(rank1)).map(Card.tupled)
      val extraCard = Card(anySuit(), rank2)
      rankHand(fourOfAKind :+ extraCard) == FourOfAKind(Vector(rank1))
    }

  property("a full house") =
    forAll(twoDifferentCardRanks, nSuits(3), nSuits(2)) { (ranks, s1, s2) =>
      val (r1, r2) = ranks
      val hand = s1.map(Card(_, r1)) ++ s2.map(Card(_, r2))
      rankHand(hand.toVector) == FullHouse(Vector(r1))
    }

  property("a flush") =
    forAll(suits, fiveNonConsecutiveCardValues) { (suit, values) =>
      rankHand(values.map(Card(suit, _))) == Flush(Vector(values.max))
    }

  property("correctly identify a straight") =
    forAll(topCardInAStraight, fiveDissimilarSuits) { (highCard, suits) =>
      val descendingCards = suits.zip(Stream.from(highCard, -1)).map(Card.tupled)
      rankHand(descendingCards.take(5).toVector) == Straight(Vector(highCard))
    }

  property("three of a kind") =
    forAll(nCardRanks(3), nSuits(3)) { (values, suits) =>
      val Seq(threeOfAKind, extraValue1, extraValue2) = values
      val hand = suits.map(Card(_, threeOfAKind)) ++ Seq(Card(anySuit(), extraValue1), Card(anySuit(), extraValue2))
      rankHand(hand.toVector) == ThreeOfAKind(Vector(threeOfAKind))
    }

  property("two pairs") =
    forAll(nCardRanks(3), nSuits(2), nSuits(2)) { (ranks, firstPairSuits, secondPairSuits) =>
      val remainingSuits = allSuits.diff(firstPairSuits).diff(secondPairSuits)
      val Seq(firstPairRank, secondPairRank, otherCardRank) = ranks
      remainingSuits.nonEmpty ==> {
        val hand = (firstPairSuits.map(Card(_, firstPairRank)) ++
          secondPairSuits.map(Card(_, secondPairRank))) :+
          Card(anySuit(remainingSuits), otherCardRank)
        rankHand(hand.toVector) == TwoPairs(ranks.slice(0, 2).sorted.reverse :+ ranks(2))
      }
    }

  property("one pair") =
    forAll(nCardRanks(4), nSuits(2)) { (values, pairSuits) =>
      val hand = pairSuits.map(Card(_, values.head)) ++ values.tail.map(Card(anySuit(), _))
      rankHand(hand.toVector) == Pair(values.head +: values.tail.sorted.reverse)
    }

  property("highest card") =
    forAll(fiveNonConsecutiveCardValues, fiveDissimilarSuits) { (values, suits) =>
      val hand = suits.zip(values).map(Card.tupled)
      rankHand(hand.toVector) == HighCard(values.sorted.reverse)
    }

  property("correctly categorise all hands") =
    forAll(handOfCards) { (hand: Vector[Card]) =>
      rankHand(hand).isInstanceOf[Rank]
    }

}

object PokerScorerProperties extends Properties("Poker hand scoring") {

  property("compare by rank regardless of tiebreakers") =
    forAll(twoDifferentHandRanks, fiveCardValues, fiveCardValues) { (ranks, tieBreaker, tieBreaker2) =>
      val (rank1, rank2) = ranks
      handsByRank(rank1)(tieBreaker).compare(handsByRank(rank2)(tieBreaker2)) ==
        rank1.compareTo(rank2)
    }

  property("tiebreak any equally ranked hand by card values") =
    forAll(handRank, fiveCardValues, fiveCardValues) { (rank, tieBreakers1, tieBreakers2) =>
      val handWith = handsByRank(rank)
      handWith(tieBreakers1).compare(handWith(tieBreakers2)) ==
        Ordering[Vector[Int]].compare(tieBreakers1, tieBreakers2)
    }

  property("reject hands with the wrong number of cards") =
    forAll(illegalHand) { (illegalHand) =>
      val result = Try(rankHand(illegalHand))
      result.isFailure && result.failed.get.isInstanceOf[IllegalArgumentException]
    }

  property("reject hands with illegal cards") =
    forAll(illegalCardRank, suits) { (illegalValue, suit) =>
        val result = Try(Card(suit, illegalValue))
        result.isFailure && result.failed.get.isInstanceOf[IllegalArgumentException]
    }
}
