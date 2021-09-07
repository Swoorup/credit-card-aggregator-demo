package creditcardaggregator.app

import cats.effect.*
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.implicits.*
import io.circe.*
import io.circe.parser.*
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import scala.concurrent.duration.*

import creditcardaggregator.mocks
import creditcardaggregator.integration.*
import creditcardaggregator.endpoints.*
import creditcardaggregator.routes.*
import creditcardaggregator.service.*
import creditcardaggregator.infrastructure.cscards.DefaultCsCardProviderConfig
import creditcardaggregator.infrastructure.scoredcards.DefaultScoredCardProviderConfig

class CreditCardAppIntegrationSuite extends CatsEffectSuite {
  private def stubCsCardsEndpoint(server: WireMockServer) =
    server.stubFor(
      post("/cs/v1/cards")
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody("""
                        |[
                        | {
                        |    "apr": 21.4,
                        |    "cardName": "SuperSaver Card",
                        |    "eligibility": 6.3
                        |  },
                        |  {
                        |    "apr": 19.2,
                        |    "cardName": "SuperSpender Card",
                        |    "eligibility": 5
                        |  }
                        |]
                      """.stripMargin)
        )
    )

  private def stubScoredCardsEndpoint(server: WireMockServer) =
    server.stubFor(
      post("/sc/v2/creditcards")
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody("""
                      |[
                      |  {
                      |    "approvalRating": 0.8,
                      |    "apr": 19.4,
                      |    "card": "ScoredCard Builder"
                      |  }
                      |]
                      """.stripMargin)
        )
    )

  private def stubBadScoredCardsEndpoint(server: WireMockServer) =
    server.stubFor(
      post("/sc/v2/creditcards")
        .willReturn(
          aResponse()
            .withStatus(500)
        )
    )

  private def stubDelayedScoredCardsEndpoint(server: WireMockServer) =
    server.stubFor(
      post("/sc/v2/creditcards")
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody("""
                      |[
                      |  {
                      |    "approvalRating": 0.8,
                      |    "apr": 19.4,
                      |    "card": "ScoredCard Builder"
                      |  }
                      |]
                      """.stripMargin)
            .withFixedDelay(10.seconds.toMillis.toInt)
        )
    )

  private def prepareConfiguration(server: WireMockServer) =
    AppConfig(
      port = server.port,
      csCardsBaseUrl = s"http://localhost:${server.port}/cs",
      scoredCardsBaseUrl = s"http://localhost:${server.port}/sc",
      csCardsProviderConfig = DefaultCsCardProviderConfig.copy(maxConnections = 2, timeout = 1.second),
      scoredCardsProviderConfig = DefaultScoredCardProviderConfig.copy(maxConnections = 2, timeout = 1.second)
    )
  private def createService(server: WireMockServer, request: Request[IO]): IO[Response[IO]] =
    App.mkHttpApp[IO](prepareConfiguration(server)).use(app => app(request))

  test("Should be able to provide a success response") {
    withWiremockServer { server =>
      stubCsCardsEndpoint(server)
      stubScoredCardsEndpoint(server)
      val request = Request[IO](Method.POST, uri"creditcards")
        .withEntity("""{
          "name": "John Smith",
          "creditScore": 500,
          "salary": 28000
          }""")

      val response = createService(server, request).unsafeRunSync()
      assertEquals(response.status, Status.Ok)
      assertEquals(
        parse(response.as[String].unsafeRunSync()),
        parse("""[
          {
              "provider": "ScoredCards",
              "name": "ScoredCard Builder",
              "apr": 19.4,
              "cardScore": 0.212
          },
          {
              "provider": "CSCards",
              "name": "SuperSaver Card",
              "apr": 21.4,
              "cardScore": 0.137
          },
          {
              "provider": "CSCards",
              "name": "SuperSpender Card",
              "apr": 19.2,
              "cardScore": 0.135
          }
      ]""")
      )
    }
  }

  test("Returns error response if one of the provider returns error") {
    withWiremockServer { server =>
      stubCsCardsEndpoint(server)
      stubBadScoredCardsEndpoint(server)
      val request = Request[IO](Method.POST, uri"creditcards")
        .withEntity("""{
          "name": "John Smith",
          "creditScore": 500,
          "salary": 28000
          }""")

      val response = createService(server, request).unsafeRunSync()
      assertEquals(response.status, Status.BadGateway)
      assertEquals(
        parse(response.as[String].unsafeRunSync()),
        parse("""{ "message":"Unable to query card partner: ScoredCards" }""")
      )
    }
  }

  test("Returns error response if one of the provider is timed out") {
    withWiremockServer { server =>
      stubCsCardsEndpoint(server)
      stubDelayedScoredCardsEndpoint(server)
      val request = Request[IO](Method.POST, uri"creditcards")
        .withEntity("""{
          "name": "John Smith",
          "creditScore": 500,
          "salary": 28000
          }""")

      val response = createService(server, request).unsafeRunSync()
      assertEquals(response.status, Status.BadGateway)
      assertEquals(
        parse(response.as[String].unsafeRunSync()),
        parse("""{ "message":"Unable to query card partner: ScoredCards" }""")
      )
    }
  }
}
