package poker

import org.scalacheck.Gen.posNum
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{EitherValues, FlatSpec, Matchers}
import poker.PokerGenerators._

import scala.math.Ordering.Implicits._

class PokerHandSpec extends FlatSpec with GeneratorDrivenPropertyChecks with Matchers with EitherValues {
  override implicit val generatorDrivenConfig = PropertyCheckConfig(minSuccessful = 500, workers = 4)

  behavior of "A hand ranking function"

  it should "correctly identify a straight flush" in {
    forAll(topCardInAStraight, suits) { (highCard, suit) =>
      val descendingCards = Stream.from(highCard, -1).map { value => Card(suit, value) }
      rankHand(descendingCards.take(5).toVector) shouldBe StraightFlush(Vector(highCard))
    }
  }

  it should "correctly identify four of a kind" in {
    forAll(twoDifferentCardRanks) { (ranks) =>
      val (rank1, rank2) = ranks
      val fourOfAKind = allSuits.zip(Vector.fill(4)(rank1)).map(Card.tupled)
      val extraCard = Card(anySuit(), rank2)
      rankHand(fourOfAKind :+ extraCard) shouldBe FourOfAKind(Vector(rank1))
    }
  }

  it should "correctly identify a full house" in {
    forAll(twoDifferentCardRanks, nSuits(3), nSuits(2)) { (ranks, s1, s2) =>
      val (r1, r2) = ranks
      val hand = s1.map(Card(_, r1)) ++ s2.map(Card(_, r2))
      rankHand(hand.toVector) shouldBe FullHouse(Vector(r1))
    }
  }

  it should "correctly identify a flush" in {
    forAll(suits, fiveNonConsecutiveCardValues) { (suit, values) =>
      rankHand(values.map(Card(suit, _))) shouldBe Flush(Vector(values.max))
    }
  }

  it should "correctly identify a straight" in {
    forAll(topCardInAStraight, fiveDissimilarSuits) { (highCard, suits) =>
      val descendingCards = suits.zip(Stream.from(highCard, -1)).map(Card.tupled)
      rankHand(descendingCards.take(5).toVector) shouldBe Straight(Vector(highCard))
    }
  }

  it should "correctly identify three of a kind" in {
    forAll(nCardRanks(3), nSuits(3)) { (values, suits) =>
      val Seq(threeOfAKind, extraValue1, extraValue2) = values
      val hand = suits.map(Card(_, threeOfAKind)) ++ Seq(Card(anySuit(), extraValue1), Card(anySuit(), extraValue2))
      rankHand(hand.toVector) shouldBe ThreeOfAKind(Vector(threeOfAKind))
    }
  }

  it should "correct identify two pairs" in {
    forAll(nCardRanks(3), nSuits(2), nSuits(2)) { (ranks, firstPairSuits, secondPairSuits) =>
      val remainingSuits = allSuits.diff(firstPairSuits).diff(secondPairSuits)
      val Seq(firstPairRank, secondPairRank, otherCardRank) = ranks
      whenever(remainingSuits.nonEmpty) {
        val hand = (firstPairSuits.map(Card(_, firstPairRank)) ++
          secondPairSuits.map(Card(_, secondPairRank))) :+
          Card(anySuit(remainingSuits), otherCardRank)
        rankHand(hand.toVector) shouldBe TwoPairs(ranks.slice(0, 2).sorted.reverse :+ ranks(2))
      }
    }
  }

  it should "correctly identify one pair" in {
    forAll(nCardRanks(4), nSuits(2)) { (values, pairSuits) =>
      val hand = pairSuits.map(Card(_, values.head)) ++ values.tail.map(Card(anySuit(), _))
      rankHand(hand.toVector) shouldBe Pair(values.head +: values.tail.sorted.reverse)
    }
  }

  it should "identify everything else as highest card" in {
    forAll(fiveNonConsecutiveCardValues, fiveDissimilarSuits) { (values, suits) =>
      val hand = suits.zip(values).map(Card.tupled)
      rankHand(hand.toVector) shouldBe HighCard(values.sorted.reverse)
    }
  }

  it should "correctly categorise all hands" in {
    forAll(handOfCards) { (hand: Vector[Card]) =>
      rankHand(hand) should be(a[Rank])
    }
  }

  behavior of "A poker hand scorer"

  it should "compare by rank regardless of tiebreakers" in {
    forAll(twoDifferentHandRanks, fiveCardValues, fiveCardValues) { (ranks, tieBreaker, tieBreaker2) =>
      val (rank1, rank2) = ranks
      handsByRank(rank1)(tieBreaker).compare(handsByRank(rank2)(tieBreaker2)) shouldBe
        rank1.compareTo(rank2)
    }
  }

  it should "tiebreak any equally ranked hand by card values" in {
    forAll(handRank, fiveCardValues, fiveCardValues) { (rank, tieBreakers1, tieBreakers2) =>
      val handWith = handsByRank(rank)
      handWith(tieBreakers1).compare(handWith(tieBreakers2)) shouldBe
        Ordering[Vector[Int]].compare(tieBreakers1, tieBreakers2)
    }
  }

  it should "reject hands with the wrong number of cards" in {
    forAll(illegalHand) { (illegalHand) =>
      an[IllegalArgumentException] should be thrownBy rankHand(illegalHand)
    }
  }

  it should "reject hands with illegal cards" in {
    forAll(posNum[Int], suits) { (illegalValue, suit) =>
      whenever(illegalValue < 2 || illegalValue > 14) {
        an[IllegalArgumentException] should be thrownBy Card(suit, illegalValue)
      }
    }
  }


}
