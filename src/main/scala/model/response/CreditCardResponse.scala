package creditcardaggregator.model.response

import io.circe.Codec
import io.circe.generic.semiauto.*
import sttp.tapir.*

case class CreditCard(
  provider: String,
  name: String,
  apr: BigDecimal,
  cardScore: BigDecimal
)

type CreditCardResponse = List[CreditCard]

object CreditCard {
  given Codec[CreditCard]  = deriveCodec[CreditCard]
  given Schema[CreditCard] = Schema.derived
}

object CreditCardResponse {
  given Codec[CreditCardResponse]  = deriveCodec[CreditCardResponse]
  given Schema[CreditCardResponse] = Schema.derived
}
