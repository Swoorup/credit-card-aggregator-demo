package creditcardaggregator.common

case class Scale(min: BigDecimal, max: BigDecimal) {

  /** Convert the value in the given scale in between 0.0 - 1.0 */
  def normalize(v: BigDecimal): BigDecimal = (v - min) / (max - min)
  def toPercent(v: BigDecimal): BigDecimal = normalize(v) * 100
}

object Scale:
  val Normalized: Scale = Scale(0, 1)
