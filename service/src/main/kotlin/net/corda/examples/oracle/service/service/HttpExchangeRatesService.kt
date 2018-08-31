package net.corda.examples.oracle.service.service

import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken

@CordaService
class HttpExchangeRatesService : SingletonSerializeAsToken() {
    private val baseUrl = "https://api.exchangeratesapi.io/latest?"

    fun getExchangeRate(fromCurrencyCode: String, toCurrencyCode: String): Double {
        val url = "{$baseUrl}base=$fromCurrencyCode&symbols=$toCurrencyCode"
        val response = khttp.get(url)
        try {
            return response.jsonObject.optJSONObject("rates").getDouble(toCurrencyCode)
        } catch (e: Exception) {
            throw IllegalArgumentException()
        }
    }
}