package net.corda.oracle.client

import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.getOrThrow
import net.corda.oracle.mock.QueryHandlerMock
import net.corda.oracle.mock.SignHandlerMock
import net.corda.oracle.base.contract.ExchangeRateState
import net.corda.oracle.client.flow.CreateExchangeRate
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNodeParameters
import net.corda.testing.node.StartedMockNode
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ExchangeRateClientTests {
    private val mockNet = MockNetwork(listOf("net.corda.oracle.service.service", "net.corda.oracle.base.contract"))
    private lateinit var a: StartedMockNode

    @Before
    fun setUp() {
        a = mockNet.createNode()

        val oracleName = CordaX500Name("Oracle", "New York", "US")
        val oracle = mockNet.createNode(MockNodeParameters(legalName = oracleName))
        listOf(QueryHandlerMock::class.java, SignHandlerMock::class.java).forEach { oracle.registerInitiatedFlow(it) }

        mockNet.runNetwork()
    }

    @After
    fun tearDown() {
        mockNet.stopNodes()
    }

    @Test
    fun `oracle returns correct exchange rate`() {
        val fromCurrencyCode = "BGN"
        val toCurrencyCode = "USD"

        val flow = a.startFlow(CreateExchangeRate(fromCurrencyCode, toCurrencyCode))
        mockNet.runNetwork()
        val result = flow.getOrThrow().tx.outputsOfType<ExchangeRateState>().single()
        assertEquals(fromCurrencyCode, result.fromCurrencyCode)
        assertEquals(toCurrencyCode, result.toCurrencyCode)
        assertEquals(0.6, result.rate)
    }

}