package net.corda.oracle.mock

import net.corda.core.node.ServiceHub
import net.corda.oracle.service.service.HttpExchangeRatesService

class HttpExchangeRatesServiceMock(service: ServiceHub) : HttpExchangeRatesService(service) {
    override fun getExchangeRate(fromCurrencyCode: String, toCurrencyCode: String): Double {
        val listOfCurrencyCodes = listOf("BGN", "USD")
        if (!(fromCurrencyCode in listOfCurrencyCodes && toCurrencyCode in listOfCurrencyCodes)) {
            throw IllegalArgumentException()
        } else {
            return 0.6
        }
    }
}