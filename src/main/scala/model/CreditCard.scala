package creditcardservice.model

import scala.math.*

case class CreditCard(
  provider: String,
  name: String,
  apr: BigDecimal,
  cardScore: BigDecimal
)

object CreditCard {
  def calculateSortingScore(eligibility: Double, apr: Double): Double = 
    eligibility * pow(1/apr, 2)

  def apply(provider: String, name: String, apr: BigDecimal, eligibility: BigDecimal): CreditCard = 
    val sortScore = calculateSortingScore(eligibility.toDouble, apr.toDouble)
    CreditCard(provider, name, apr, sortScore)
}