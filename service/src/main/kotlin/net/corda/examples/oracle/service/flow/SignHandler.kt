package net.corda.examples.oracle.service.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.transactions.FilteredTransaction
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap
import net.corda.examples.oracle.base.flow.SignExchangeRate
import net.corda.examples.oracle.service.service.ExchangeRatesOracle

@InitiatedBy(SignExchangeRate::class)
open class SignHandler(val session: FlowSession) : FlowLogic<Unit>() {
    companion object {
        object RECEIVING : ProgressTracker.Step("Receiving sign request.")
        object SIGNING : ProgressTracker.Step("Signing filtered transaction.")
        object SENDING : ProgressTracker.Step("Sending sign response.")
    }

    override val progressTracker = ProgressTracker(RECEIVING, SIGNING, SENDING)

    open fun exchangeRatesOracle() = serviceHub.cordaService(ExchangeRatesOracle::class.java)

    @Suspendable
    override fun call() {
        progressTracker.currentStep = RECEIVING
        val request = session.receive<FilteredTransaction>().unwrap { it }

        progressTracker.currentStep = SIGNING
        val response = try {
            exchangeRatesOracle().sign(request)
        } catch (e: Exception) {
            throw FlowException(e)
        }

        progressTracker.currentStep = SENDING
        session.send(response)
    }
}
