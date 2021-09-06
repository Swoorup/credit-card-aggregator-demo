package creditcardaggregator.infrastructure.scoredcards.client

import io.circe.Codec
import io.circe.generic.semiauto.*
import creditcardaggregator.model.CreditCard
import creditcardaggregator.infrastructure.config.CardProviderConfig

case class ScoredCardsResponse(
  card: String,
  apr: Double,
  approvalRating: Double
) derives Codec.AsObject {
  def toCreditCard(config: CardProviderConfig): CreditCard =
    CreditCard(
      provider = config.providerName,
      name = card,
      apr = apr,
      eligibility = config.eligibilityScale.normalize(approvalRating)
    )
}
