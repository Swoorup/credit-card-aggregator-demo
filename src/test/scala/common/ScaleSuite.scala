package creditcardaggregator.common

import munit.ScalaCheckSuite
import org.scalacheck.Prop.*
import org.scalacheck.Gen

class ScaleSuite extends ScalaCheckSuite:
  property("Value in one scale can be mapped to another correctly") {
    val zeroToTen = Scale(0, 10)
    val input     = Gen.choose(zeroToTen.min, zeroToTen.max)

    forAll(input) { (d: BigDecimal) =>
      val normalized = zeroToTen.normalize(d)
      normalized >= 0 && normalized <= 1
    }
  }
