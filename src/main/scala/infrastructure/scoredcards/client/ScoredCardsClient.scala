package creditcardaggregator.infrastructure.scoredcards.client

import cats.*
import cats.implicits.*
import cats.effect.*
import sttp.client3.*
import sttp.client3.circe.*
import io.circe.Codec
import org.asynchttpclient.{AsyncHttpClientConfig, DefaultAsyncHttpClientConfig}
import sttp.client3.asynchttpclient.fs2.AsyncHttpClientFs2Backend
import sttp.client3.SttpBackend

import creditcardaggregator.model.{CreditCard, User}
import creditcardaggregator.service.{CreditCardPartnerApi, CreditCardPartnerApiCard}
import creditcardaggregator.infrastructure.config.CardProviderConfig

object ScoredCardsClient {
  def apply[F[_]: Async](baseUrl: String, providerConfig: CardProviderConfig): Resource[F, ScoredCardsClient[F]] = {
    val conf: AsyncHttpClientConfig = new DefaultAsyncHttpClientConfig.Builder()
      .setMaxConnections(providerConfig.maxConnections)
      .setRequestTimeout(providerConfig.timeout.toMillis.toInt)
      .setReadTimeout(providerConfig.timeout.toMillis.toInt)
      .build()

    AsyncHttpClientFs2Backend
      .resourceUsingConfig(conf)
      .map(backend => new ScoredCardsClient(baseUrl, providerConfig, backend))
  }
}

class ScoredCardsClient[F[_]](
  baseUrl: String,
  config: CardProviderConfig,
  backend: SttpBackend[F, Any]
)(using F: MonadError[F, Throwable])
    extends CreditCardPartnerApi[F] {

  override def partnerName      = config.providerName
  override def eligibilityScale = config.eligibilityScale

  override def listCreditCards(user: User): F[List[CreditCardPartnerApiCard]] = {
    val uri = uri"$baseUrl/v2/creditcards"
    for {
      response <- basicRequest
                    .post(uri)
                    .readTimeout(config.timeout)
                    .response(asJson[List[ScoredCardsResponse]])
                    .body(ScoredCardsRequest(user))
                    .send(backend)
      cards <- response.body.liftTo[F]
    } yield cards
  }
}
