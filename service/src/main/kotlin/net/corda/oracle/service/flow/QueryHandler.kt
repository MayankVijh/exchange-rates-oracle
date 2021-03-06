package net.corda.oracle.service.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap
import net.corda.oracle.base.flow.QueryExchangeRate
import net.corda.oracle.service.service.ExchangeRatesOracle

@InitiatedBy(QueryExchangeRate::class)
open class QueryHandler(val session: FlowSession) : FlowLogic<Unit>() {
    companion object {
        object RECEIVING : ProgressTracker.Step("Receiving query request.")
        object CALCULATING : ProgressTracker.Step("Calculating exchange rate.")
        object SENDING : ProgressTracker.Step("Sending query response.")
    }

    override val progressTracker = ProgressTracker(RECEIVING, CALCULATING, SENDING)

    open fun exchangeRatesOracle() = serviceHub.cordaService(ExchangeRatesOracle::class.java)

    @Suspendable
    override fun call() {
        progressTracker.currentStep = RECEIVING
        val request = session.receive<Pair<String, String>>().unwrap { it }

        progressTracker.currentStep = CALCULATING
        val response = try {
            exchangeRatesOracle().query(request.first, request.second)
        } catch (e: Exception) {
            // Re-throw the exception as a FlowException so its propagated to the querying node.
            throw FlowException(e)
        }

        progressTracker.currentStep = SENDING
        session.send(response)
    }
}