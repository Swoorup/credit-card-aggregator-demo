package creditcardaggregator.model.response

import io.circe.Encoder
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
  given Encoder[CreditCard] = deriveEncoder[CreditCard]
  given Schema[CreditCard]  = Schema.derived
}

object CreditCardResponse {
  given Encoder[CreditCardResponse] = deriveEncoder[CreditCardResponse]
  given Schema[CreditCardResponse]  = Schema.derived
}
