package creditcardaggregator

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options

package object integration {
  def withWiremockServer(block: WireMockServer => Any) = {
    val wireMockServer = new WireMockServer(options().dynamicPort().dynamicHttpsPort())
    wireMockServer.start()
    block(wireMockServer)
    wireMockServer.stop()
  }
}
