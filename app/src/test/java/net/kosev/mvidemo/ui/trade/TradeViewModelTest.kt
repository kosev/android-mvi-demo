package net.kosev.mvidemo.ui.trade

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import net.kosev.mvidemo.getOrAwaitValue
import net.kosev.mvidemo.repository.Balances
import net.kosev.mvidemo.repository.BalancesRepository
import net.kosev.mvidemo.repository.PriceRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import java.math.BigDecimal

@ExperimentalCoroutinesApi
class TradeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()

    private val balancesRepo = mock<BalancesRepository>()
    private val priceRepo = mock<PriceRepository>()
    private val amountFormatter = mock<AmountFormatter>()

    private val tested = TradeViewModel(balancesRepo, priceRepo, amountFormatter)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `onEvent with SettingsClick should emit navigate to setting effect`() {
        tested.onEvent(TradeEvent.SettingsClick)
        val effect = tested.effect.getOrAwaitValue()
        assertEquals(TradeEffect.NavigateToSettings, effect.getContentIfNotHandled())
    }

    @Test
    fun `onEvent with ScreenLoad should emit success state`() = dispatcher.runBlockingTest {
        whenever(balancesRepo.getBalances()).thenReturn(Balances(BigDecimal.ONE, BigDecimal.TEN))
        whenever(priceRepo.getCryptoPrice()).thenReturn(BigDecimal.TEN)
        whenever(amountFormatter.formatCryptoWithSymbol(eq(BigDecimal.ONE))).thenReturn("BTC 1.00000000")
        whenever(amountFormatter.formatFiatWithSymbol(eq(BigDecimal.TEN))).thenReturn("EUR 10.00")
        whenever(amountFormatter.formatExchangeRate(eq(BigDecimal.TEN))).thenReturn("BTC 1 = EUR 10.00")
        whenever(amountFormatter.formatCrypto(eq(BigDecimal.ZERO))).thenReturn("0.00000000")

        tested.onEvent(TradeEvent.ScreenLoad)

        inOrder(balancesRepo, priceRepo) {
            verify(balancesRepo).getBalances()
            verify(priceRepo).getCryptoPrice()
            verifyNoMoreInteractions()
        }

        val expected = TradeState.Success(
            cryptoBalance = "BTC 1.00000000",
            fiatBalance = "EUR 10.00",
            cryptoPrice = BigDecimal.TEN,
            rate = "BTC 1 = EUR 10.00",
            result = "0.00000000"
        )
        assertEquals(expected, tested.state.getOrAwaitValue())
    }

    @Test
    fun `onEvent with ScreenLoad and failing balances should emit error state`() = dispatcher.runBlockingTest {
        whenever(balancesRepo.getBalances()).thenThrow(RuntimeException())

        tested.onEvent(TradeEvent.ScreenLoad)

        assertEquals(TradeState.Error, tested.state.getOrAwaitValue())
    }

}
