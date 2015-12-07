package poker

import org.scalacheck.Gen
import org.scalacheck.Gen._
import poker.Util._

import scala.util.Random

object PokerGenerators {

  val handRank: Gen[HandRank] = ???
  val cardRank: Gen[CardRank] = ???
  val topCardInAStraight: Gen[CardRank] = ???
  val suits: Gen[Suit] = ???

  def nSuits(n: Int): Gen[Vector[Suit]] = ???

  def nCardRanks(n: Int): Gen[Vector[CardRank]] = ???

  val fiveCardValues: Gen[Vector[CardRank]] = ???
  val handOfCards: Gen[Vector[Card]] = ???
  val twoDifferentHandRanks: Gen[(HandRank, HandRank)] = ???

  val twoDifferentCardRanks: Gen[(CardRank, CardRank)] = ???

  val fiveNonConsecutiveCardValues:Gen[Vector[CardRank]] = ???

  val fiveDissimilarSuits: Gen[Seq[Suit]] = ???

  val illegalHand: Gen[Vector[Card]] = ???
  val illegalCardRank: Gen[Int] = ???

  def anySuit(suits: Seq[Suit] = allSuits) = Random.shuffle(allSuits).head

}
