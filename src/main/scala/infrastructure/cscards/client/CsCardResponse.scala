package creditcardaggregator.infrastructure.cscards.client

import io.circe.Decoder
import io.circe.generic.semiauto.*
import creditcardaggregator.model.CreditCard
import creditcardaggregator.infrastructure.config.CardProviderConfig
import creditcardaggregator.service.CreditCardPartnerApiCard

case class CsCardResponse(
  cardName: String,
  apr: Double,
  eligibility: Double
) extends CreditCardPartnerApiCard {

  override def name = cardName
}

given Decoder[CsCardResponse] = deriveDecoder[CsCardResponse]
