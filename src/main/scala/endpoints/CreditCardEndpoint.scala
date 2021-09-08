package creditcardaggregator.endpoints

import cats.effect.Async
import cats.implicits.*
import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint
import sttp.model.StatusCode

import creditcardaggregator.service.*
import creditcardaggregator.model.request.CreditCardRequest
import creditcardaggregator.model.response.{CreditCard, CreditCardResponse, ErrorResponse}
import creditcardaggregator.model.{CreditCard as CreditCardDomain, User}

extension (req: CreditCardRequest)
  private def toUser: User = User(
    name = req.name,
    creditScore = req.creditScore,
    salary = req.salary
  )

class CreditCardEndpoints[F[_]: Async](ccAggregator: CreditCardAggregatorService[F]) {
  val findCreditCards: ServerEndpoint[CreditCardRequest, ErrorResponse, CreditCardResponse, Any, F] =
    RootEndpoint.root.post
      .description("Find credit cards for a given user")
      .in("creditcards")
      .in(jsonBody[CreditCardRequest])
      .out(jsonBody[CreditCardResponse])
      .serverLogic(request => ccAggregator.aggregateCreditCards(request.toUser).map(toCreditCardResponse))

  private def toCreditCardResponse(
    result: Either[CreditCardAggregatorError, List[CreditCardDomain]]
  ): Either[ErrorResponse, CreditCardResponse] =
    result.fold(
      { case CreditCardAggregatorError.ProviderError(provider) =>
        ErrorResponse.CreditCardProviderErrorResponse(s"Unable to query card partner: $provider").asLeft
      },
      _.map(card =>
        CreditCard(
          card.provider,
          card.name,
          card.apr,
          card.cardScore.setScale(3, BigDecimal.RoundingMode.DOWN)
        )
      ).asRight
    )
}
