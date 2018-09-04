package net.corda.oracle.mock

import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.oracle.base.flow.SignExchangeRate
import net.corda.oracle.service.flow.SignHandler

@InitiatedBy(SignExchangeRate::class)
class SignHandlerMock(session: FlowSession): SignHandler(session) {
    override fun exchangeRatesOracle() = ExchangeRatesOracleMock(serviceHub)
}