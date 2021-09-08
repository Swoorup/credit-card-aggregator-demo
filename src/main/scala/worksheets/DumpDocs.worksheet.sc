import creditcardaggregator.service.CreditCardAggregatorService
import $ivy.`com.softwaremill.sttp.tapir::tapir-openapi-docs:0.18.3`
import $ivy.`com.softwaremill.sttp.tapir::tapir-openapi-circe:0.18.3`
import $ivy.`com.softwaremill.sttp.tapir::tapir-openapi-circe-yaml:0.18.3`

import creditcardaggregator.infrastructure.cscards
import cats.effect.*
import cats.implicits.*
import cats.effect.unsafe.IORuntime
import cats.{Monad, Parallel}
import com.monovore.decline.*
import com.monovore.decline.effect.*
import creditcardaggregator.app.*
import java.io.File
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import scala.concurrent.ExecutionContext.global

import sttp.client3.*
import sttp.client3.circe.*
import sttp.client3.asynchttpclient.fs2.AsyncHttpClientFs2Backend
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.circe.yaml._
import sttp.client3.SttpBackend

import creditcardaggregator.model.*
import creditcardaggregator.common.*
import creditcardaggregator.endpoints.*
import creditcardaggregator.infrastructure.cscards.client.*
import creditcardaggregator.infrastructure.cscards.DefaultCsCardProviderConfig
import creditcardaggregator.infrastructure.config.*

given IORuntime = IORuntime.global

(for {
  csClient <- CsCardsClient[IO]("https://app.clearscore.com/api/global/backend-tech-test", DefaultCsCardProviderConfig)
  aggregator = CreditCardAggregatorService(csClient)
  endpoints = new CreditCardEndpoints(aggregator)
  docs = OpenAPIDocsInterpreter().toOpenAPI(endpoints.findCreditCards, "My Bookshop", "1.0")
} yield docs)
.use(docs => IO(println(docs.toYaml)))
.unsafeRunSync()