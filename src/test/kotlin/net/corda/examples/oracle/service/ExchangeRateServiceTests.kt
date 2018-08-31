package net.corda.examples.oracle.service

import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndContract
import net.corda.core.identity.CordaX500Name
import net.corda.core.transactions.TransactionBuilder
import net.corda.examples.oracle.HttpExchangeRatesServiceMock
import net.corda.examples.oracle.base.contract.EXCHANGE_RATE_PROGRAM_ID
import net.corda.examples.oracle.base.contract.ExchangeRateContract
import net.corda.examples.oracle.base.contract.ExchangeRateState
import net.corda.examples.oracle.service.service.ExchangeRateOracle
import net.corda.testing.core.SerializationEnvironmentRule
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import org.junit.Rule
import org.junit.Test
import java.util.function.Predicate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExchangeRateServiceTests {
    private val oracleIdentity = TestIdentity(CordaX500Name("Oracle", "New York", "US"))
    private val dummyServices = MockServices(listOf("net.corda.examples.oracle.base.contract"), oracleIdentity)
    private val oracle = ExchangeRateOracle(dummyServices, HttpExchangeRatesServiceMock())
    private val aliceIdentity = TestIdentity(CordaX500Name("Alice", "", "GB"))
    private val notaryIdentity = TestIdentity(CordaX500Name("Notary", "", "GB"))

    @Rule
    @JvmField
    val testSerialization = SerializationEnvironmentRule()

    @Test
    fun `oracle returns correct exchange rate`() {
        assertEquals(0.6, oracle.query("BGN", "USD"))
    }

    @Test
    fun `oracle rejects invalid currency codes`() {
        assertFailsWith<IllegalArgumentException> { oracle.query("BGNinvalid", "USD") }
        assertFailsWith<IllegalArgumentException> { oracle.query("BGN", "USDinvalid") }
    }

    @Test
    fun `oracle signs transactions including valid currency codes and rate`() {
        val command = Command(ExchangeRateContract.Create("BGN", "USD", 0.6), listOf(oracleIdentity.publicKey))
        val state = ExchangeRateState("BGN", "USD", 0.6, aliceIdentity.party)
        val stateAndContract = StateAndContract(state, EXCHANGE_RATE_PROGRAM_ID)
        val ftx = TransactionBuilder(notaryIdentity.party)
                .withItems(stateAndContract, command)
                .toWireTransaction(dummyServices)
                .buildFilteredTransaction(Predicate {
                    when (it) {
                        is Command<*> -> oracle.services.myInfo.legalIdentities.first().owningKey in it.signers && it.value is ExchangeRateContract.Create
                        else -> false
                    }
                })
        val signature = oracle.sign(ftx)
        assert(signature.verify(ftx.id))
    }

    @Test
    fun `oracle does not sign transactions including an invalid rate`() {
        val command = Command(ExchangeRateContract.Create("BGN", "USD", 0.7), listOf(oracleIdentity.publicKey))
        val state = ExchangeRateState("BGN", "USD", 0.6, aliceIdentity.party)
        val stateAndContract = StateAndContract(state, EXCHANGE_RATE_PROGRAM_ID)
        val ftx = TransactionBuilder(notaryIdentity.party)
                .withItems(stateAndContract, command)
                .toWireTransaction(oracle.services)
                .buildFilteredTransaction(Predicate {
                    when (it) {
                        is Command<*> -> oracle.services.myInfo.legalIdentities.first().owningKey in it.signers && it.value is ExchangeRateContract.Create
                        else -> false
                    }
                })
        assertFailsWith<IllegalArgumentException> { oracle.sign(ftx) }
    }
}