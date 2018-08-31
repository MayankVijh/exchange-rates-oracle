package net.corda.examples.oracle.base.contract

import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.transactions.LedgerTransaction

const val EXCHANGE_RATE_PROGRAM_ID: ContractClassName = "net.corda.examples.oracle.base.contract.ExchangeRateContract"

class ExchangeRateContract : Contract {
    class Create(val fromCurrencyCode: String, val toCurrencyCode: String, val rate: Double) : CommandData

    override fun verify(tx: LedgerTransaction) = requireThat {
        "There are no inputs" using (tx.inputs.isEmpty())
        val output = tx.outputsOfType<ExchangeRateState>().single()
        val command = tx.commands.requireSingleCommand<Create>().value

        "The currency codes in the output do not match the currency codes in the command." using
                (command.fromCurrencyCode == output.fromCurrencyCode && command.toCurrencyCode == output.toCurrencyCode)
    }
}

data class ExchangeRateState(val fromCurrencyCode: String,
                             val toCurrencyCode: String,
                             val rate: Double,
                             val requester: AbstractParty) : ContractState {
    override val participants: List<AbstractParty> get() = listOf(requester)
    override fun toString() = "$fromCurrencyCode to $toCurrencyCode is $rate."
}