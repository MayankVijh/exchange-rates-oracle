package net.corda.examples.oracle.client.plugin

import net.corda.examples.oracle.client.api.ClientApi
import net.corda.webserver.services.WebServerPluginRegistry
import java.util.function.Function

// Web-server plugin that registers our static web content and web API.
class WebPlugin : WebServerPluginRegistry {
    override val webApis = listOf(Function(::ClientApi))

    override val staticServeDirs: Map<String, String> = mapOf(
            "exchange-rate" to javaClass.classLoader.getResource("exchangeRatesWeb").toExternalForm()
    )
}