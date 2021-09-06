package creditcardaggregator.app

import cats.{Monad, Parallel}
import cats.implicits.*
import cats.effect.{Async, Resource}
import org.http4s.HttpApp

import creditcardaggregator.endpoints.CreditCardEndpoints
import creditcardaggregator.routes.CreditCardRoutes
import creditcardaggregator.service.CreditCardAggregatorService
import creditcardaggregator.infrastructure.cscards.DefaultCsCardProviderConfig
import creditcardaggregator.infrastructure.scoredcards.DefaultScoredCardProviderConfig
import creditcardaggregator.infrastructure.cscards.client.*
import creditcardaggregator.infrastructure.scoredcards.client.*

case class AppConfig(
  port: Int,
  cscardsBaseUrl: String,
  scoredCardsBaseUrl: String
)

object App {
  def mkHttpApp[F[_]: Async: Monad: Parallel](config: AppConfig): Resource[F, HttpApp[F]] =
    for {
      csCardClient     <- CsCardsClient(config.cscardsBaseUrl, DefaultCsCardProviderConfig)
      scoredCardClient <- ScoredCardsClient(config.scoredCardsBaseUrl, DefaultScoredCardProviderConfig)
      aggregatorService = CreditCardAggregatorService(csCardClient, scoredCardClient)
      endpoints         = new CreditCardEndpoints(aggregatorService)
      routes            = CreditCardRoutes(endpoints)
    } yield Server.forRoutes(routes)
}
