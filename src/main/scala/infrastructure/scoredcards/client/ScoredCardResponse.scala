package creditcardaggregator.infrastructure.scoredcards.client

import io.circe.Decoder
import io.circe.generic.semiauto.*
import creditcardaggregator.model.CreditCard
import creditcardaggregator.infrastructure.config.CardProviderConfig
import creditcardaggregator.service.CreditCardPartnerApiCard

case class ScoredCardsResponse(
  card: String,
  apr: Double,
  approvalRating: Double
) extends CreditCardPartnerApiCard {
  override def name        = card
  override def eligibility = approvalRating
}

given Decoder[ScoredCardsResponse] = deriveDecoder[ScoredCardsResponse]
