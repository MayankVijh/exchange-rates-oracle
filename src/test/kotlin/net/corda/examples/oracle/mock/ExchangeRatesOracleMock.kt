package net.corda.examples.oracle.mock

import net.corda.core.node.ServiceHub
import net.corda.core.node.services.CordaService
import net.corda.examples.oracle.service.service.ExchangeRatesOracle

@CordaService
class ExchangeRatesOracleMock(services: ServiceHub): ExchangeRatesOracle(services) {
    override fun httpExchangeRatesService() = HttpExchangeRatesServiceMock(services)
}