package creditcardaggregator.endpoints

import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint
import sttp.model.StatusCode
import io.circe.generic.auto.*
import sttp.tapir.generic.auto.*

import creditcardaggregator.model.response.ErrorResponse

object RootEndpoint {
  val root: Endpoint[Unit, ErrorResponse, Unit, Any] =
    endpoint.errorOut(errorResponseMapping)

  private lazy val errorResponseMapping: EndpointOutput.Single[ErrorResponse] =
    oneOf[ErrorResponse](
      statusMapping(
        StatusCode.BadGateway,
        jsonBody[ErrorResponse.CreditCardProviderErrorResponse].description("Bad gateway")
      ),
      statusMapping(
        StatusCode.InternalServerError,
        jsonBody[ErrorResponse.InternalErrorResponse].description("Internal server error")
      )
    )
}
