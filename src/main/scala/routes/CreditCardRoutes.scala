package creditcardaggregator.routes

import cats.*
import cats.effect.*

import creditcardaggregator.endpoints.CreditCardEndpoints
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.*
import sttp.tapir.server.http4s.Http4sServerInterpreter

object CreditCardRoutes {
  def apply[F[_]: Async](userEndpoints: CreditCardEndpoints[F]): Routes[F] =
    new Routes[F] {
      override val routes: HttpRoutes[F] =
        Http4sServerInterpreter[F]().toRoutes(userEndpoints.findCreditCards)
    }
}
