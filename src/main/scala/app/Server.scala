package creditcardaggregator.app

import cats.effect.Async
import org.http4s.HttpApp
import creditcardaggregator.routes.Routes

object Server {
  import cats.syntax.semigroupk._
  import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

  def forRoutes[F[_]: Async](first: Routes[F], remaining: Routes[F]*): HttpApp[F] =
    (first +: remaining)
      .map(_.routes)
      .reduceLeft(_ <+> _)
      .orNotFound
}
