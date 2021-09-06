package creditcardaggregator.infrastructure.cscards.client

import cats.*
import cats.implicits.*
import cats.effect.*
import sttp.client3.*
import sttp.client3.circe.*
import io.circe.Codec
import io.circe.generic.semiauto.*
import org.asynchttpclient.{AsyncHttpClientConfig, DefaultAsyncHttpClientConfig}
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client3.SttpBackend
import sttp.capabilities.fs2.Fs2Streams

import creditcardaggregator.model.{CreditCard, User}
import creditcardaggregator.service.CreditCardPartnerApi
import creditcardaggregator.infrastructure.config.CardProviderConfig

object CsCardsClient {
  def apply[F[_]: Async](baseUrl: String, providerConfig: CardProviderConfig): Resource[F, CsCardsClient[F]] = {
    val conf: AsyncHttpClientConfig = new DefaultAsyncHttpClientConfig.Builder()
      .build()

    AsyncHttpClientCatsBackend
      .resourceUsingConfig(conf)
      .map(backend => new CsCardsClient(baseUrl, providerConfig, backend))
  }
}

class CsCardsClient[F[_]](
  baseUrl: String,
  config: CardProviderConfig,
  backend: SttpBackend[F, Any]
)(using F: MonadError[F, Throwable])
    extends CreditCardPartnerApi[F] {
  override def listCreditCards(user: User): F[List[CreditCard]] = {
    val uri = uri"$baseUrl/v1/cards"
    for {
      response <- basicRequest
                    .body(CsCardRequest(user))
                    .post(uri)
                    .response(asJson[List[CsCardResponse]])
                    .send(backend)
      body <- F.fromEither(response.body)
    } yield body.map(_.toCreditCard(config))
  }
}
