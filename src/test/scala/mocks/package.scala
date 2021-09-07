package creditcardaggregator

import cats.effect.*
import creditcardaggregator.service.*
import creditcardaggregator.common.Scale
import creditcardaggregator.model.*

package object mocks {
  case class MockProviderCard(
    name: String,
    apr: Double,
    eligibility: Double
  ) extends CreditCardPartnerApiCard

  def createCardMockProvider(
    ret: IO[List[MockProviderCard]],
    name: String = "ScoredCards",
    scale: Scale = Scale.Normalized
  ): CreditCardPartnerApi[IO] =
    new CreditCardPartnerApi[IO]:
      def partnerName                 = name
      def eligibilityScale            = scale
      def listCreditCards(user: User) = ret
}
