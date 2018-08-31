package net.corda.examples.oracle.base.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.identity.Party
import net.corda.core.utilities.unwrap

@InitiatingFlow
class QueryExchangeRate(val oracle: Party, val fromCurrencyCode: String, val toCurrencyCode: String) : FlowLogic<Double>() {
    @Suspendable override fun call() = initiateFlow(oracle).sendAndReceive<Double>(Pair(fromCurrencyCode, toCurrencyCode)).unwrap { it }
}