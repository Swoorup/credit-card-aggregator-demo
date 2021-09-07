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
import sttp.client3.SttpBackend

import creditcardaggregator.model.*
import creditcardaggregator.common.*
import creditcardaggregator.infrastructure.cscards.client.*
import creditcardaggregator.infrastructure.cscards.DefaultCsCardProviderConfig
import creditcardaggregator.infrastructure.config.*

given IORuntime = IORuntime.global

val john = User("John", 12, 300)

DefaultCsCardProviderConfig.eligibilityScale.toPercent(6.3)

// val csClient = CsCardsClient[IO]("https://app.clearscore.com/api/global/backend-tech-test", DefaultCsCardProviderConfig)
// csClient.use(_.listCreditCards(john)).unsafeRunSync()

// AsyncHttpClientFs2Backend
//   .resource[IO]()
//   .use { backend =>
//     for {
//       response <- basicRequest
//         .response(asJson[List[CsCardResponse]])
//         .post(uri"https://app.clearscore.com/api/global/backend-tech-test/v1/cards")
//         .body(CsCardRequest(john))
//         .send(backend)
//       body <- response.body.liftTo[IO]
//     } yield body
//   }
//   .unsafeRunSync()