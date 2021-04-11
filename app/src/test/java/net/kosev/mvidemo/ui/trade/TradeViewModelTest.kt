package net.kosev.mvidemo.ui.trade

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import net.kosev.mvidemo.getOrAwaitValue
import net.kosev.mvidemo.repository.BalancesRepository
import net.kosev.mvidemo.repository.PriceRepository
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class TradeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val balancesRepo = mock<BalancesRepository>()
    private val priceRepo = mock<PriceRepository>()
    private val amountFormatter = mock<AmountFormatter>()

    private val tested = TradeViewModel(balancesRepo, priceRepo, amountFormatter)

    @Test
    fun `onEvent with SettingsClick should emit navigate to setting effect`() {
        tested.onEvent(TradeEvent.SettingsClick)
        val effect = tested.effect.getOrAwaitValue()
        assertEquals(TradeEffect.NavigateToSettings, effect.getContentIfNotHandled())
    }

}
