package creditcardservice.endpoints

import cats.effect.Async
import cats.implicits.*
import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint
import sttp.model.StatusCode
import io.circe.generic.auto.*

import creditcardservice.service.CreditCardAggregatorService
import creditcardservice.model.request.CreditCardRequest
import creditcardservice.model.response.{CreditCard, CreditCardResponse, ErrorResponse}
import creditcardservice.model.{CreditCard as CreditCardDomain, User}

extension (req: CreditCardRequest)
  private def toUser: User = User(
    name = req.name,
    creditScore = req.creditScore,
    salary = req.salary
  )

class CreditCardEndpoints[F[_]: Async](creditCardService: CreditCardAggregatorService[F]) {
  val findCreditCards: ServerEndpoint[CreditCardRequest, ErrorResponse, CreditCardResponse, Any, F] =
    RootEndpoint.root.post
      .description("Find credit cards for a given user")
      .in("creditcards")
      .in(jsonBody[CreditCardRequest])
      .out(jsonBody[CreditCardResponse])
      .serverLogic(request => creditCardService.recommend(request.toUser).map(toCreditCardResponse))

  private def toResponse[I, R](input: Option[I], f: I => R, error: ErrorResponse): Either[ErrorResponse, R] =
    input.fold[Either[ErrorResponse, R]](Left(error))(input => Right(f(input)))

  private def toCreditCardResponse(cards: List[CreditCardDomain]): Either[ErrorResponse, CreditCardResponse] =
    cards.map(card => CreditCard(card.provider, card.name, card.apr, card.cardScore)).asRight
}
