package creditcardservice

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*
import com.monovore.decline.*
import com.monovore.decline.effect.*
import java.io.File

case class AppConfig(
  port: Int,
  cscardsBaseUrl: String,
  scoredCardsBaseUrl: String
)

val appConfigOpts: Opts[AppConfig] = {
  val port                = Opts.option[Int]("port", "The port to expose service on.", short = "p")
  val cscardsEndpoint     = Opts.option[String]("cs-cards-api", "The base url for CSCards.", short = "c")
  val scoredCardsEndpoint = Opts.option[String]("scored-cards-api", "The base url for ScoredCards.", short = "s")
  (port, cscardsEndpoint, scoredCardsEndpoint).mapN(AppConfig.apply)
}

object CreditRecommenderApi
    extends CommandIOApp(
      name = "CreditRecommenderApi",
      header = "Credit Recommender Api"
    ) {

  override def main: Opts[IO[ExitCode]] =
    appConfigOpts.map { config =>
      for {
        _ <- IO.println("whaterver")
      } yield ExitCode.Success
    }
}
