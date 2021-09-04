package creditcardservice.endpoints

import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint
import sttp.model.StatusCode
import io.circe.generic.auto.*
import sttp.tapir.generic.auto.*

import creditcardservice.model.response.ErrorResponse

object RootEndpoint {
  val root: Endpoint[Unit, ErrorResponse, Unit, Any] =
    endpoint.errorOut(errorResponseMapping)

  private lazy val errorResponseMapping: EndpointOutput.Single[ErrorResponse] =
    oneOf[ErrorResponse](
      statusMapping(
        StatusCode.NotFound,
        jsonBody[ErrorResponse.NotFoundResponse].description("Not found")
      ),
      statusMapping(
        StatusCode.BadRequest,
        jsonBody[ErrorResponse.BadRequestResponse].description("Bad request")
      ),
      statusMapping(
        StatusCode.InternalServerError,
        jsonBody[ErrorResponse.InternalErrorResponse].description("Internal server error")
      )
    )
}
