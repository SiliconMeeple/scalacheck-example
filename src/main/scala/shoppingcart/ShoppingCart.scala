package shoppingcart

object ShoppingCart {
  type Item = String
  type MissingItems = List[String]
  type MissingItemsOr[A] = Either[MissingItems, A]
  type ItemAndQty = (Item, Int)
  type PricingRule = Int => Int

  def price(deals: Map[Item, PricingRule])(items: Seq[Item]): MissingItemsOr[Int] = {
    def validateItem(item: (Item, Int)): Either[String, ItemAndQty] =
      if (deals.contains(item._1)) {
        Right(item)
      } else {
        Left(s"No price for item ${item._1}")
      }

    val validatedItems = items.groupBy(identity).mapValues(_.size).map(validateItem).toList
    val pricesOrErrors = collectItemsOrErrors(validatedItems)
    pricesOrErrors.right.map(_.map { case (sku, amount) => deals(sku)(amount) }.sum)
  }

  def collectItemsOrErrors(validatedItems: List[Either[String, ItemAndQty]]): Either[MissingItems, List[ItemAndQty]] = {
    validatedItems.foldLeft(Right(List.empty[ItemAndQty]): Either[MissingItems, List[ItemAndQty]]) {
      case (Left(errs), Left(err)) => Left(err +: errs)
      case (Left(errs), _) => Left(errs)
      case (Right(is), Right(i)) => Right(i +: is)
      case (_, Left(err)) => Left(List(err))
    }
  }
}
