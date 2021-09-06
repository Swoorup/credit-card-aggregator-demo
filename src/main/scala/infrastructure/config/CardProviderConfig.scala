package creditcardaggregator.infrastructure.config

import creditcardaggregator.common.Scale

case class CardProviderConfig(
  providerName: String,
  eligibilityScale: Scale
)
