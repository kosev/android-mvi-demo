package net.kosev.mvidemo.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalancesRepository(private val defaultDispatcher: CoroutineDispatcher) {

    private var currentCryptoBalance = BigDecimal("1.23450000")
    private var currentFiatBalance = BigDecimal("36456.00")

    @Inject
    constructor() : this(Dispatchers.IO)

    suspend fun getBalances(): Balances =
        withContext(defaultDispatcher) {
            delay(1500)
            Balances(currentCryptoBalance, currentFiatBalance)
        }

    suspend fun buyCrypto(fiatAmount: BigDecimal, price: BigDecimal): Unit =
        withContext(defaultDispatcher) {
            delay(100)
            currentFiatBalance -= fiatAmount
            currentCryptoBalance += fiatAmount.divide(price, 8, RoundingMode.HALF_UP)
        }

}

data class Balances(
    val cryptoBalance: BigDecimal,
    val fiatBalance: BigDecimal
)
