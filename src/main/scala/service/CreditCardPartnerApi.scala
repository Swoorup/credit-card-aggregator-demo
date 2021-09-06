package creditcardaggregator.service

import creditcardaggregator.model.{CreditCard, User}

trait CreditCardPartnerApi[F[_]]:
  def listCreditCards(user: User): F[List[CreditCard]]
