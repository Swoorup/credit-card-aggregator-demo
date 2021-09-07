package creditcardaggregator.app

import cats.{Monad, Parallel}
import cats.implicits.*
import cats.effect.{Async, Resource}
import org.http4s.HttpApp

import creditcardaggregator.endpoints.CreditCardEndpoints
import creditcardaggregator.routes.CreditCardRoutes
import creditcardaggregator.service.CreditCardAggregatorService
import creditcardaggregator.infrastructure.config.CardProviderConfig
import creditcardaggregator.infrastructure.cscards.client.*
import creditcardaggregator.infrastructure.scoredcards.client.*

case class AppConfig(
  port: Int,
  csCardsBaseUrl: String,
  csCardsProviderConfig: CardProviderConfig,
  scoredCardsBaseUrl: String,
  scoredCardsProviderConfig: CardProviderConfig
)

object App {
  def mkHttpApp[F[_]: Async: Monad: Parallel](config: AppConfig): Resource[F, HttpApp[F]] =
    for {
      csCardClient     <- CsCardsClient(config.csCardsBaseUrl, config.csCardsProviderConfig)
      scoredCardClient <- ScoredCardsClient(config.scoredCardsBaseUrl, config.scoredCardsProviderConfig)
      aggregatorService = CreditCardAggregatorService(scoredCardClient, csCardClient)
      endpoints         = new CreditCardEndpoints(aggregatorService)
      routes            = CreditCardRoutes(endpoints)
    } yield Server.forRoutes(routes)
}
