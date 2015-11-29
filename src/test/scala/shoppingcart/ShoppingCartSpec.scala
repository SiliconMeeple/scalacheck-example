package shoppingcart

import org.scalacheck.Gen
import org.scalacheck.Gen.{listOf, alphaStr, nonEmptyListOf, posNum}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{EitherValues, FlatSpec, Matchers}
import shoppingcart.ShoppingCart._
import shoppingcart.StreamOps.unfold

import scala.util.Random

class ShoppingCartSpec extends FlatSpec with GeneratorDrivenPropertyChecks with Matchers with EitherValues {

  import shoppingcart.ShoppingCartSpec._

  behavior of "A shopping cart"

  it should "be free when empty" in {
    priceWithTestItems(List.empty[Item]) should be(Right(0))
  }

  it should "sum a shopping cart with no deals" in {
    forAll(posNum[Int]) { (totalPrice: Int) =>
      val shoppingList = shoppingListWithTotalAndItems(totalPrice, testItems)
      priceWithTestItems(shoppingList) should be(Right(totalPrice))
    }
  }

  it should "complain about all unknown items" in {
    forAll(nonEmptyListOf(alphaStr)) { (unknownItems: List[Item]) =>
      val expectedErrors = unknownItems.distinct.map(item => s"No price for item $item")
      price(Map.empty)(unknownItems).left.value should contain theSameElementsAs expectedErrors
    }
  }

  val applesAndPears: Gen[Item] = Gen.oneOf("Apple", "Pear")

  it should "apply a bogof deal" in {
    def bogof(quantity: Int) = (quantity + 1) / 2
    val priceCart = price(Map("Apple" -> bogof, "Pear" -> simplePrice(10))) _

    forAll(listOf(applesAndPears)) { (cart: List[Item]) =>
      priceCart(cart ++ List("Apple", "Apple")) should be(priceCart(cart).right.map(_ + 1))
    }
  }

  it should "apply different deals separately" in {
    def threeForTwo(quantity: Int) = ((quantity / 3) * 2)  + (quantity % 3)
    val priceCart = price(Map("Apple" -> threeForTwo, "Pear" -> threeForTwo)) _

    forAll(listOf(Gen.const("Apple"))) { (cart: List[Item]) =>
      priceCart("Pear" +: cart) should be(priceCart(cart).right.map(_ + 1))
    }
  }

}

object ShoppingCartSpec {
  def simplePrice(price: Int)(amount: Int) = price * amount

  val testItems: Map[Item, PricingRule] =
    Map("A" -> 1, "B" -> 10, "C" -> 100,
      "D" -> 1000, "E" -> 10000, "F" -> 100000,
      "G" -> 1000000, "H" -> 10000000, "I" -> 100000000).mapValues(price => simplePrice(price))

  def priceWithTestItems = price(testItems) _

  def shoppingListWithTotalAndItems(totalPrice: Int, items: Map[Item, PricingRule]) = {
    def step(price: Int): Option[(String, Int)] = {
      if (price == 0)
        None
      else {
        val itemAndPrice = pickRandomElement(items.filter { case (sku, rule) => rule(1) <= price })
        Some((itemAndPrice._1, price - itemAndPrice._2(1)))
      }
    }

    unfold(totalPrice)(step)
  }

  def pickRandomElement[A, B](coll: Map[A, B]): (A, B) = coll.toList(Random.nextInt(coll.size))

}
