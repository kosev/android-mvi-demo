package net.kosev.mvidemo.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalancesRepository(private val defaultDispatcher: CoroutineDispatcher) {

    @Inject
    constructor() : this(Dispatchers.IO)

    suspend fun getBalances(): Balances = withContext(defaultDispatcher) {
        delay(2000)
        Balances(BigDecimal("1.23450000"), BigDecimal("36456.00"))
    }

}

data class Balances(
    val cryptoBalance: BigDecimal,
    val fiatBalance: BigDecimal
)
