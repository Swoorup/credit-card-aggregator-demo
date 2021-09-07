package creditcardaggregator.app

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits.*
import com.monovore.decline.*
import com.monovore.decline.effect.*
import java.io.File
import org.http4s.blaze.server.BlazeServerBuilder
import scala.concurrent.ExecutionContext.global

import creditcardaggregator.app.*
import creditcardaggregator.infrastructure.cscards.DefaultCsCardProviderConfig
import creditcardaggregator.infrastructure.scoredcards.DefaultScoredCardProviderConfig

object CreditAggregatorApi
    extends CommandIOApp(
      name = "CreditAggregatorApi",
      header = "Credit Aggregator Api"
    ) {

  val appConfigOpts: Opts[AppConfig] = {
    val port                = Opts.option[Int]("port", "The port to expose service on.", short = "p")
    val cscardsEndpoint     = Opts.option[String]("cs-cards-api", "The base url for CSCards.", short = "c")
    val scoredCardsEndpoint = Opts.option[String]("scored-cards-api", "The base url for ScoredCards.", short = "s")
    (port, cscardsEndpoint, scoredCardsEndpoint).mapN { case (port, cscardsEndpoint, scoredCardsEndpoint) =>
      AppConfig(
        port = port,
        csCardsBaseUrl = cscardsEndpoint,
        csCardsProviderConfig = DefaultCsCardProviderConfig,
        scoredCardsBaseUrl = scoredCardsEndpoint,
        scoredCardsProviderConfig = DefaultScoredCardProviderConfig
      )
    }
  }

  def run(config: AppConfig): IO[ExitCode] =
    App
      .mkHttpApp[IO](config)
      .use(app =>
        val serverBuilder = BlazeServerBuilder[IO](global)
          .bindHttp(config.port, "localhost")
          .withHttpApp(app)

        for
          fiber <- serverBuilder.resource.use(_ => IO.never).start
          _     <- IO.readLine
          _     <- fiber.cancel
        yield ExitCode.Success
      )

  override def main: Opts[IO[ExitCode]] =
    appConfigOpts.map { config =>
      for {
        _ <- run(config)
      } yield ExitCode.Success
    }
}
