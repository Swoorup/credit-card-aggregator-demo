package creditcardaggregator.service

import cats.*
import cats.implicits.*
import cats.effect.*
import scala.math.pow
import creditcardaggregator.model.{CreditCard, User}

enum CreditCardAggregatorError:
  case ProviderError(provider: String)

trait CreditCardAggregatorService[F[_]]:
  def aggregateCreditCards(user: User): F[Either[CreditCardAggregatorError, List[CreditCard]]]

object CreditCardAggregatorService {
  extension [F[_]: Monad](partner: CreditCardPartnerApi[F])(using MonadError[F, Throwable])
    private def listForUser(user: User): F[Either[CreditCardAggregatorError, List[CreditCard]]] =
      for {
        cards <- partner.listCreditCards(user).attempt
      } yield cards.fold(
        _ => CreditCardAggregatorError.ProviderError(partner.partnerName).asLeft,
        _.map(cc =>
          CreditCard.fromEligibility(
            provider = partner.partnerName,
            name = cc.name,
            apr = cc.apr,
            eligibility = partner.eligibilityScale.toPercent(cc.eligibility)
          )
        ).asRight
      )

  def apply[F[_]: Monad: Parallel](
    firstPartner: CreditCardPartnerApi[F],
    remaining: CreditCardPartnerApi[F]*
  )(using F: MonadError[F, Throwable]): CreditCardAggregatorService[F] = {

    val partners = firstPartner +: remaining
    new CreditCardAggregatorService[F]:
      def aggregateCreditCards(user: User): F[Either[CreditCardAggregatorError, List[CreditCard]]] =
        for {
          responses <- partners.parTraverse(_.listForUser(user))
          cards = responses.sequence.map(_.flatten.toList)
        } yield cards
  }
}
