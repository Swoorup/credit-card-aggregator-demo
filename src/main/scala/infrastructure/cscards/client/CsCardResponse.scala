package creditcardaggregator.infrastructure.cscards.client

import io.circe.Codec
import io.circe.generic.semiauto.*
import creditcardaggregator.model.CreditCard
import creditcardaggregator.infrastructure.config.CardProviderConfig

case class CsCardResponse(
  cardName: String,
  apr: Double,
  eligibility: Double
) derives Codec.AsObject {
  def toCreditCard(config: CardProviderConfig): CreditCard =
    CreditCard(
      provider = config.providerName,
      name = cardName,
      apr = apr,
      eligibility = config.eligibilityScale.normalize(eligibility)
    )
}
