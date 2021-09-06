package creditcardaggregator.infrastructure.scoredcards

import creditcardaggregator.common.Scale
import creditcardaggregator.infrastructure.config.CardProviderConfig

val DefaultScoredCardProviderConfig: CardProviderConfig =
  CardProviderConfig("ScoredCards", Scale.Normalized)
