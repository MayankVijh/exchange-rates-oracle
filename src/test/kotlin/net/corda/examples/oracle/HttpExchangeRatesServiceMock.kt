package net.corda.examples.oracle

import net.corda.examples.oracle.service.service.HttpExchangeRatesService

class HttpExchangeRatesServiceMock : HttpExchangeRatesService() {
    override fun getExchangeRate(fromCurrencyCode: String, toCurrencyCode: String): Double {
        val listOfCurrencyCodes = listOf("BGN", "USD")
        if (!(fromCurrencyCode in listOfCurrencyCodes && toCurrencyCode in listOfCurrencyCodes)) {
            throw IllegalArgumentException()
        } else {
            return 0.6
        }
    }
}