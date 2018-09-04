package net.corda.examples.oracle.mock

import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.examples.oracle.base.flow.QueryExchangeRate
import net.corda.examples.oracle.service.flow.QueryHandler

@InitiatedBy(QueryExchangeRate::class)
class QueryHandlerMock(session: FlowSession): QueryHandler(session) {
    override fun exchangeRatesOracle() = ExchangeRatesOracleMock(serviceHub)
}