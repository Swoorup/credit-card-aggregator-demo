package creditcardaggregator.model.request

import io.circe.Codec
import io.circe.generic.semiauto.*
import sttp.tapir.*

case class CreditCardRequest(
  name: String,
  creditScore: Int,
  salary: Int
)

object CreditCardRequest {
  given Codec[CreditCardRequest] = deriveCodec[CreditCardRequest]

  given validator: Validator[CreditCardRequest] = Validator.all(
    Validator.min(0).contramap(_.salary),
    Validator.min(0).contramap(_.creditScore),
    Validator.max(700).contramap(_.creditScore)
  )

  given Schema[CreditCardRequest] = Schema.derived[CreditCardRequest].validate(validator)
}
