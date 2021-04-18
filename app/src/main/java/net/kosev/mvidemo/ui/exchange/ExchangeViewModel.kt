package net.kosev.mvidemo.ui.exchange

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.kosev.mvidemo.repository.BalancesRepository
import net.kosev.mvidemo.repository.PriceRepository
import net.kosev.mvidemo.ui.AmountFormatter
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val balancesRepository: BalancesRepository,
    private val priceRepository: PriceRepository,
    private val amountFormatter: AmountFormatter
) : ViewModel() {
}
