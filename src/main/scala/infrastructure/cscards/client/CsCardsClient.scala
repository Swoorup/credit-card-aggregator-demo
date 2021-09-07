package creditcardaggregator.infrastructure.cscards.client

import cats.*
import cats.implicits.*
import cats.effect.*
import sttp.client3.*
import sttp.client3.circe.*
import io.circe.Codec
import org.asynchttpclient.{AsyncHttpClientConfig, DefaultAsyncHttpClientConfig}
import sttp.client3.asynchttpclient.fs2.AsyncHttpClientFs2Backend
import sttp.client3.SttpBackend
import sttp.capabilities.fs2.Fs2Streams

import creditcardaggregator.model.{CreditCard, User}
import creditcardaggregator.service.{CreditCardPartnerApi, CreditCardPartnerApiCard}
import creditcardaggregator.infrastructure.config.CardProviderConfig

object CsCardsClient {
  def apply[F[_]: Async](baseUrl: String, providerConfig: CardProviderConfig): Resource[F, CsCardsClient[F]] = {
    val conf: AsyncHttpClientConfig = new DefaultAsyncHttpClientConfig.Builder()
      .setMaxConnections(providerConfig.maxConnections)
      .setRequestTimeout(providerConfig.timeout.toMillis.toInt)
      .setReadTimeout(providerConfig.timeout.toMillis.toInt)
      .build()

    AsyncHttpClientFs2Backend
      .resourceUsingConfig(conf)
      .map(backend => new CsCardsClient(baseUrl, providerConfig, backend))
  }
}

class CsCardsClient[F[_]](
  baseUrl: String,
  config: CardProviderConfig,
  backend: SttpBackend[F, Fs2Streams[F]]
)(using F: MonadError[F, Throwable])
    extends CreditCardPartnerApi[F] {

  override def partnerName      = config.providerName
  override def eligibilityScale = config.eligibilityScale

  override def listCreditCards(user: User): F[List[CreditCardPartnerApiCard]] =
    for {
      response <- basicRequest
                    .response(asJson[List[CsCardResponse]])
                    .readTimeout(config.timeout)
                    .post(uri"$baseUrl/v1/cards")
                    .body(CsCardRequest(user))
                    .send(backend)
      cards <- response.body.liftTo[F]
    } yield cards
}
