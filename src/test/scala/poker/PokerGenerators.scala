package poker

import org.scalacheck.Gen
import org.scalacheck.Gen._
import poker.Util._

import scala.util.Random

object PokerGenerators {

  val handRank = chooseNum(0, 8)
  val cardRank = chooseNum(2, 14)
  val topCardInAStraight = chooseNum(6, 14)
  val suits: Gen[Suit] = oneOf(allSuits)

  def nSuits(n: Int) = pick(n, allSuits).map(_.toVector)

  def nCardRanks(n: Int) = pick(n, 2 to 14).map(_.toVector)

  val card = for {
    suit <- suits
    value <- cardRank
  } yield Card(suit, value)

  val fiveCardValues = resize(5, containerOf[Vector, CardRank](cardRank))
  val handOfCards = containerOfN[Vector, Card](5, card)
  val twoDifferentHandRanks: Gen[(Int, Int)] = for {
    rank1 <- handRank
    rank2 <- handRank
    if rank1 != rank2
  } yield (rank1, rank2)

  val twoDifferentCardRanks: Gen[(CardRank, CardRank)] = for {
    rank1 <- cardRank
    rank2 <- cardRank
    if rank1 != rank2
  } yield (rank1, rank2)

  val fiveNonConsecutiveCardValues = nCardRanks(5).suchThat(values =>
    !values.sorted.reverse.differences.forall(_ == 1)
  )

  val fiveDissimilarSuits = Gen.listOfN(5, suits).suchThat(_.toSet.size > 1)

  val illegalHand = containerOf[Vector, Card](card).suchThat(_.size != 5)
  val illegalCardRank = posNum[Int].suchThat(v => v < 2 || v > 14)

  def anySuit(suits: Seq[Suit] = allSuits) = Random.shuffle(allSuits).head

}
