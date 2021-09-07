package creditcardaggregator.infrastructure.config

import creditcardaggregator.common.Scale
import scala.concurrent.duration.*

case class CardProviderConfig(
  providerName: String,
  eligibilityScale: Scale,
  timeout: FiniteDuration = 5.seconds,
  maxConnections: Int = 100
)
