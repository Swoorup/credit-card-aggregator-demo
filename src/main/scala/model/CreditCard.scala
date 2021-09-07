package creditcardaggregator.model

import scala.math.*

case class CreditCard(
  provider: String,
  name: String,
  apr: BigDecimal,
  cardScore: BigDecimal
)

object CreditCard {
  def calculateSortingScore(eligibility: BigDecimal, apr: BigDecimal): BigDecimal = eligibility * (1.0 / apr).pow(2)

  def fromEligibility(provider: String, name: String, apr: BigDecimal, eligibility: BigDecimal): CreditCard =
    val sortScore = calculateSortingScore(eligibility, apr)
    CreditCard(provider, name, apr, sortScore)
}
