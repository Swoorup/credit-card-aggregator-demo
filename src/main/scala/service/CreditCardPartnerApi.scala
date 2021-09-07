package creditcardaggregator.service

import creditcardaggregator.model.{CreditCard, User}
import creditcardaggregator.common.Scale

trait CreditCardPartnerApiCard:
  def name: String
  def apr: Double
  def eligibility: Double

trait CreditCardPartnerApi[F[_]]:
  def partnerName: String
  def eligibilityScale: Scale
  def listCreditCards(user: User): F[List[CreditCardPartnerApiCard]]
