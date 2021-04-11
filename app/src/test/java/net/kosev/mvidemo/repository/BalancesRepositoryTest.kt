package net.kosev.mvidemo.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

@ExperimentalCoroutinesApi
class BalancesRepositoryTest {

    private val dispatcher = TestCoroutineDispatcher()
    private val tested = BalancesRepository(dispatcher)

    @After
    fun tearDown() = dispatcher.cleanupTestCoroutines()

    @Test
    fun `getBalances should return the hardcoded balances`() = dispatcher.runBlockingTest {
        assertEquals(
            Balances(BigDecimal("1.23450000"), BigDecimal("36456.00")),
            tested.getBalances()
        )
    }

}
