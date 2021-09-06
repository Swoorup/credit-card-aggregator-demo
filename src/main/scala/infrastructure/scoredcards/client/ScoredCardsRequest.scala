package creditcardaggregator.infrastructure.scoredcards.client

import io.circe.Codec
import io.circe.generic.semiauto.*
import creditcardaggregator.model.User

case class ScoredCardsRequest(
  name: String,
  score: Int,
  salary: Int
) derives Codec.AsObject

object ScoredCardsRequest:
  def apply(user: User): ScoredCardsRequest =
    ScoredCardsRequest(
      user.name,
      user.creditScore,
      user.salary
    )
