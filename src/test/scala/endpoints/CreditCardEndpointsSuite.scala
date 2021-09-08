package creditcardaggregator.endpoints

import cats.effect.*
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.implicits.*
import io.circe.*
import io.circe.parser.*

import creditcardaggregator.mocks
import creditcardaggregator.endpoints.*
import creditcardaggregator.routes.*
import creditcardaggregator.service.*

class CreditCardEndpointsSuite extends CatsEffectSuite:
  private def createCardMockProvider(isGoodProvider: Boolean): CreditCardPartnerApi[IO] =
    if isGoodProvider then mocks.createCardMockProvider(IO(List(mocks.MockProviderCard("ScoredCard Builder", 19.4, 0.8))))
    else mocks.createCardMockProvider(IO.raiseError(RuntimeException("provider issue")))

  private def createService(isProviderGood: Boolean, request: Request[IO]): IO[Response[IO]] =
    val aggregatorService = CreditCardAggregatorService[IO](createCardMockProvider(isProviderGood))
    val endpoints         = new CreditCardEndpoints[IO](aggregatorService)
    val routes            = CreditCardRoutes[IO](endpoints)
    routes.routes.orNotFound(request)

  test("Returns ok response if provider succeeds") {
    val request = Request[IO](Method.POST, uri"creditcards")
      .withEntity("""{
        "name": "John Smith",
        "creditScore": 500,
        "salary": 28000
        }""")

    val response = createService(true, request).unsafeRunSync()
    assertEquals(response.status, Status.Ok)
    assertEquals(
      parse(response.as[String].unsafeRunSync()),
      parse("""[
      { 
        "provider":"ScoredCards",
        "name":"ScoredCard Builder",
        "apr":19.4,
        "cardScore":0.212
      }
    ]""")
    )
  }

  test("Returns bad request if validation on user request failed") {
    val request = Request[IO](Method.POST, uri"creditcards")
      .withEntity("""{
        "name": "John Smith",
        "creditScore": 999,
        "salary": 28000
        }""")

    val response = createService(true, request).unsafeRunSync()
    assertEquals(response.status, Status.BadRequest)
  }

  test("Returns bad gateway error response if provider has error") {
    val request = Request[IO](Method.POST, uri"creditcards")
      .withEntity("""{
        "name": "John Smith",
        "creditScore": 500,
        "salary": 28000
        }""")

    val response = createService(false, request).unsafeRunSync()
    assertEquals(response.status, Status.BadGateway)
    assertEquals(
      parse(response.as[String].unsafeRunSync()),
      parse("""{ "message":"Unable to query card partner: ScoredCards" }""")
    )
  }
