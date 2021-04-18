package net.kosev.mvidemo.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import net.kosev.mvidemo.getOrAwaitValue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val tested = HomeViewModel()

    @Test
    fun `onEvent with LiveDataDemoClick should navigate to live data demo`() {
        tested.onEvent(HomeEvent.LiveDataDemoClick)

        assertEquals(
            HomeEffect.NavigateToLiveDataDemo,
            tested.effect.getOrAwaitValue().getContentIfNotHandled()
        )
    }

    @Test
    fun `onEvent with FlowDemoClick should navigate to flow demo`() {
        tested.onEvent(HomeEvent.FlowDemoClick)

        assertEquals(
            HomeEffect.NavigateToFlowDemo,
            tested.effect.getOrAwaitValue().getContentIfNotHandled()
        )
    }

}
