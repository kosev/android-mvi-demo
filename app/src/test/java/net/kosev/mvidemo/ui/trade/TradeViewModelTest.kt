package net.kosev.mvidemo.ui.trade

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import net.kosev.mvidemo.R
import net.kosev.mvidemo.getOrAwaitValue
import net.kosev.mvidemo.repository.Balances
import net.kosev.mvidemo.repository.BalancesRepository
import net.kosev.mvidemo.repository.PriceRepository
import net.kosev.mvidemo.ui.AmountFormatter
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
        tested.setEvent(TradeEvent.SettingsClick)
        assertEquals(
            TradeEffect.NavigateToSettings,
            tested.effect.getOrAwaitValue().getContentIfNotHandled()
        )
    }

    @Test
    fun `onEvent with ScreenLoad should emit success state`() = dispatcher.runBlockingTest {
        whenever(balancesRepo.getBalances()).thenReturn(Balances(BigDecimal.ONE, BigDecimal.TEN))
        whenever(priceRepo.getCryptoPrice()).thenReturn(BigDecimal.TEN)
        whenever(amountFormatter.formatCryptoWithSymbol(eq(BigDecimal.ONE))).thenReturn("BTC 1.00000000")
        whenever(amountFormatter.formatFiatWithSymbol(eq(BigDecimal.TEN))).thenReturn("EUR 10.00")
        whenever(amountFormatter.formatExchangeRate(eq(BigDecimal.TEN))).thenReturn("BTC 1 = EUR 10.00")
        whenever(amountFormatter.formatCrypto(eq(BigDecimal.ZERO))).thenReturn("0.00000000")

        tested.setEvent(TradeEvent.ScreenLoad)

        inOrder(balancesRepo, priceRepo) {
            verify(balancesRepo).getBalances()
            verify(priceRepo).getCryptoPrice()
            verifyNoMoreInteractions()
        }

        val expected = generateSuccessState()
        assertEquals(expected, tested.state.getOrAwaitValue())
    }

    @Test
    fun `onEvent with ScreenLoad and failing balances should emit error state`() = dispatcher.runBlockingTest {
        whenever(balancesRepo.getBalances()).thenThrow(RuntimeException())

        tested.setEvent(TradeEvent.ScreenLoad)

        assertEquals(TradeState.Error, tested.state.getOrAwaitValue())
    }

    @Test
    fun `onEvent with AmountChange and empty value should show the default result`() {
        whenever(amountFormatter.formatCrypto(eq(BigDecimal.ZERO))).thenReturn("0.00000000")
        tested.setStateForTesting(generateSuccessState())

        tested.setEvent(TradeEvent.AmountChange(""))

        assertEquals(generateSuccessState(), tested.state.getOrAwaitValue())
    }

    @Test
    fun `onEvent with AmountChange and enough balance should calculate the new result`() {
        whenever(amountFormatter.formatCrypto(eq(BigDecimal("1.50000000"))))
            .thenReturn("1.50000000")
        tested.setStateForTesting(generateFundedSuccessState())

        tested.setEvent(TradeEvent.AmountChange("15"))

        val expected = generateFundedSuccessState().copy(
            amount = BigDecimal(15),
            formattedResult = "1.50000000",
            isBuyingAllowed = true
        )
        assertEquals(expected, tested.state.getOrAwaitValue())
    }

    @Test
    fun `onEvent with AmountChange and not enough balance should disable buy and show error`() {
        whenever(amountFormatter.formatCrypto(eq(BigDecimal("1.50000000"))))
            .thenReturn("1.50000000")
        tested.setStateForTesting(generateSuccessState())

        tested.setEvent(TradeEvent.AmountChange("15"))

        val expected = generateSuccessState().copy(
            amount = BigDecimal(15),
            formattedResult = "1.50000000",
            isBuyingAllowed = false,
            noBalanceError = R.string.no_balance_error
        )
        assertEquals(expected, tested.state.getOrAwaitValue())
    }

    @Test
    fun `onEvent with BuyCryptoClick should call buy crypto`() = dispatcher.runBlockingTest {
        whenever(balancesRepo.buyCrypto(eq(BigDecimal.ZERO), eq(BigDecimal.TEN))).thenReturn(Unit)
        tested.setStateForTesting(generateSuccessState())

        tested.setEvent(TradeEvent.BuyCryptoClick)

        verify(balancesRepo).buyCrypto(eq(BigDecimal.ZERO), eq(BigDecimal.TEN))
    }

    @Test
    fun `onEvent with BuyCryptoClick that throws error should show error dialog`() = dispatcher.runBlockingTest {
        whenever(balancesRepo.buyCrypto(eq(BigDecimal.ZERO), eq(BigDecimal.TEN))).thenThrow(RuntimeException())
        tested.setStateForTesting(generateSuccessState())

        tested.setEvent(TradeEvent.BuyCryptoClick)

        assertEquals(TradeEffect.ShowBuyError, tested.effect.getOrAwaitValue().getContentIfNotHandled())
    }

    private fun generateSuccessState(): TradeState.Success =
        TradeState.Success(
            formattedCryptoBalance = "BTC 1.00000000",
            formattedFiatBalance = "EUR 10.00",
            fiatBalance = BigDecimal.TEN,
            cryptoPrice = BigDecimal.TEN,
            formattedExchangeRate = "BTC 1 = EUR 10.00",
            amount = BigDecimal.ZERO,
            formattedResult = "0.00000000",
            isBuyingAllowed = false,
            noBalanceError = null
        )

    private fun generateFundedSuccessState(): TradeState.Success =
        generateSuccessState().copy(
            formattedFiatBalance = "EUR 100.00",
            fiatBalance = BigDecimal(100)
        )

}
