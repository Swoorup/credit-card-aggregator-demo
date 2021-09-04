package creditcardservice.model.response

sealed trait ErrorResponse:
  def message: String

object ErrorResponse {
  enum ServerErrorResponse extends ErrorResponse:
    case InternalErrorResponse(message: String = "Internal Server Error")

  enum CreditCardErrorResponse extends ErrorResponse:
    case BadRequestResponse(message: String = "Bad Request")
    case NotFoundResponse(message: String = "Resource not found")

  export CreditCardErrorResponse.*
  export ServerErrorResponse.*
}
