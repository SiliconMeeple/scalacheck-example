package poker

import org.scalacheck.Gen
import org.scalacheck.Gen._
import poker.Util._

import scala.util.Random

object PokerGenerators {
  // These are presented in order of usage in PokerHandProperties.
  // Not ease to write.

  val topCardInAStraight: Gen[CardRank] = ???
  val suits: Gen[Suit] = ???

  val twoDifferentHandRanks: Gen[(HandRank, HandRank)] = ???

  val fiveNonConsecutiveCardValues:Gen[Vector[CardRank]] = ???

  val fiveDissimilarSuits: Gen[Seq[Suit]] = ???

  def nCardRanks(n: Int): Gen[Vector[CardRank]] = ???
  def nSuits(n: Int): Gen[Vector[Suit]] = ???

  val handOfCards: Gen[Vector[Card]] = ???

  val twoDifferentCardRanks: Gen[(CardRank, CardRank)] = ???

  val fiveCardValues: Gen[Vector[CardRank]] = ???

  val handRank: Gen[HandRank] = ???

  val illegalHand: Gen[Vector[Card]] = ???
  val illegalCardRank: Gen[Int] = ???

  def anySuit(suits: Seq[Suit] = allSuits): Suit = ???
}
