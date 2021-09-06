package creditcardaggregator.infrastructure.cscards.client

import io.circe.Codec
import io.circe.generic.semiauto.*
import creditcardaggregator.model.User

case class CsCardRequest(
  name: String,
  creditScore: Int
) derives Codec.AsObject

object CsCardRequest:
  def apply(user: User): CsCardRequest = CsCardRequest(user.name, user.creditScore)
