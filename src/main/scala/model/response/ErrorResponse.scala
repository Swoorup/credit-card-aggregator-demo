package creditcardaggregator.model.response

sealed trait ErrorResponse:
  def message: String

object ErrorResponse {
  enum ServerErrorResponse extends ErrorResponse:
    case InternalErrorResponse(message: String = "Internal Server Error")

  case class CreditCardProviderErrorResponse(message: String) extends ErrorResponse
  export ServerErrorResponse.*
}
