package creditcardaggregator.infrastructure.cscards

import creditcardaggregator.common.Scale
import creditcardaggregator.infrastructure.config.CardProviderConfig

val DefaultCsCardProviderConfig: CardProviderConfig = CardProviderConfig("CSCards", Scale(0, 10))
