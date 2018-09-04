package net.corda.oracle.mock

import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.oracle.base.flow.QueryExchangeRate
import net.corda.oracle.service.flow.QueryHandler

@InitiatedBy(QueryExchangeRate::class)
class QueryHandlerMock(session: FlowSession): QueryHandler(session) {
    override fun exchangeRatesOracle() = ExchangeRatesOracleMock(serviceHub)
}