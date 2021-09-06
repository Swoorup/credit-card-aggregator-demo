package creditcardaggregator.service

import cats.*
import cats.implicits.*
import cats.effect.*
import scala.math.pow
import creditcardaggregator.model.{CreditCard, User}

enum CreditCardAggregatorError:
  case ProviderError(message: String)
  case UpstreamValidation(message: String)

trait CreditCardAggregatorService[F[_]]:
  def aggregateCreditCards(user: User): F[List[CreditCard]]

object CreditCardAggregatorService {
  def apply[F[_]: Monad: Parallel](
    firstPartner: CreditCardPartnerApi[F],
    remaining: CreditCardPartnerApi[F]*
  )(using F: MonadError[F, Throwable]): CreditCardAggregatorService[F] = {
    val partners = firstPartner +: remaining
    new CreditCardAggregatorService[F]:
      def aggregateCreditCards(user: User): F[List[CreditCard]] =
        for {
          responses <- partners.parTraverse(_.listCreditCards(user))
          cards = responses.flatten.toList
        } yield cards
  }
}
