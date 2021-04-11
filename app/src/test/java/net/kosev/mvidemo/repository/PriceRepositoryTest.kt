package net.kosev.mvidemo.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

@ExperimentalCoroutinesApi
class PriceRepositoryTest {

    private val dispatcher = TestCoroutineDispatcher()
    private val tested = PriceRepository(dispatcher)

    @After
    fun tearDown() = dispatcher.cleanupTestCoroutines()

    @Test
    fun `getCryptoPrice should return the hardcoded Bitcoin price`() = dispatcher.runBlockingTest {
        assertEquals(BigDecimal("50268.47"), tested.getCryptoPrice())
    }

}
