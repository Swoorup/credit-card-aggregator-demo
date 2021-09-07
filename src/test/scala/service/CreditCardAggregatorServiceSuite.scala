package creditcardaggregator.service

import cats.effect.*
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.implicits.*
import io.circe.*
import io.circe.parser.*

import creditcardaggregator.mocks
import creditcardaggregator.common.*
import creditcardaggregator.model.*
import creditcardaggregator.service.*
import cats.data.EitherT

class CreditCardAggregatorServiceSuite extends CatsEffectSuite:
  val sampleScoredCards = List(mocks.MockProviderCard("ScoredCard Builder", 19.4, 0.8))

  val sampleCsCards = List(
    mocks.MockProviderCard("SuperSaver Card", 21.4, 6.3),
    mocks.MockProviderCard("SuperSpender Card", 19.2, 5.0)
  )

  val sampleScoredCardProvider = mocks.createCardMockProvider(IO(sampleScoredCards))
  val sampleCsCardsProvider    = mocks.createCardMockProvider(IO(sampleCsCards), "CSCards", Scale(0, 10))
  val badCardsProvider         = mocks.createCardMockProvider(IO.raiseError(RuntimeException("Bad")))

  private def aggregateCC(user: User, first: CreditCardPartnerApi[IO], providers: CreditCardPartnerApi[IO]*) =
    (for {
      cards <- EitherT(CreditCardAggregatorService[IO](first, providers*).aggregateCreditCards(user))
      rounded = cards.map(cc => cc.copy(cardScore = cc.cardScore.setScale(3, BigDecimal.RoundingMode.DOWN)))
    } yield rounded).value

  test("Returns list of cards successfully when all providers respond") {
    aggregateCC(User("John Smith", 500, 28000), sampleCsCardsProvider, sampleScoredCardProvider)
      .assertEquals(
        Right(
          List(
            CreditCard(
              provider = "CSCards",
              name = "SuperSaver Card",
              apr = 21.4,
              cardScore = 0.137
            ),
            CreditCard(
              provider = "CSCards",
              name = "SuperSpender Card",
              apr = 19.2,
              cardScore = 0.135
            ),
            CreditCard(
              provider = "ScoredCards",
              name = "ScoredCard Builder",
              apr = 19.4,
              cardScore = 0.212
            )
          )
        )
      )
  }

  test("Returns provider if one of the provider raises an exception") {
    aggregateCC(User("John Smith", 500, 28000), badCardsProvider, sampleCsCardsProvider)
      .assertEquals(Left(CreditCardAggregatorError.ProviderError(provider = "ScoredCards")))
  }
