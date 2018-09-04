package net.corda.oracle.service.service

import net.corda.core.contracts.Command
import net.corda.core.crypto.TransactionSignature
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import net.corda.core.transactions.FilteredTransaction
import net.corda.oracle.base.contract.ExchangeRateContract

@CordaService
open class ExchangeRatesOracle(val services: ServiceHub) : SingletonSerializeAsToken() {
    private val myKey = services.myInfo.legalIdentities.first().owningKey

    open fun httpExchangeRatesService() = services.cordaService(HttpExchangeRatesService::class.java)

    fun query(fromCurrencyCode: String, toCurrencyCode: String): Double {
        return httpExchangeRatesService().getExchangeRate(fromCurrencyCode, toCurrencyCode)
    }

    fun sign(ftx: FilteredTransaction): TransactionSignature {
        // Check the partial Merkle tree is valid.
        ftx.verify()

        fun validate(elem: Any) = when {
            elem is Command<*> && elem.value is ExchangeRateContract.Create -> {
                val cmdData = elem.value as ExchangeRateContract.Create
                myKey in elem.signers && query(cmdData.fromCurrencyCode, cmdData.toCurrencyCode) == cmdData.rate
            }
            else -> false
        }

        // Is it a Merkle tree we are willing to sign over?
        val isValidMerkleTree = ftx.checkWithFun(::validate)

        if (isValidMerkleTree) {
            return services.createSignature(ftx, myKey)
        } else {
            throw IllegalArgumentException("Oracle signature rejected over invalid transaction.")
        }
    }
}