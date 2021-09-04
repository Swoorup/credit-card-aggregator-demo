package creditcardservice.service

import scala.math.pow
import creditcardservice.model.{CreditCard, User}

enum CreditCardError:
  case UpstreamTimeout(message: String)
  case UpstreamValidation(message: String)

trait CreditCardPartnerApi[F[_]]:
  def search(user: User): F[Either[List[CreditCard], CreditCardError]]

trait CreditCardAggregatorService[F[_]]:
  def recommend(user: User): F[List[CreditCard]]

object CreditCardAggregatorService {
  import cats.*
  import cats.implicits.*
  import cats.effect.*

  def apply[F[_]: Monad: Parallel](firstPartner: CreditCardPartnerApi[F], remaining: CreditCardPartnerApi[F]*): CreditCardAggregatorService[F] = {
    val partners = firstPartner +: remaining
    new CreditCardAggregatorService[F]:
      def recommend(user: User): F[List[CreditCard]] = 
        for {
          responses <- partners.parTraverse(_.search(user))
          cards = responses.flatten.toList
        } yield cards
  }
}