package shoppingcart

object StreamOps {
  def unfold[A, B](seed: A)(f: A => Option[(B, A)]): Stream[B] =
    f(seed) match {
      case None => Stream.empty
      case Some((b, a)) => Stream.cons(b, unfold(a)(f))
    }
}
