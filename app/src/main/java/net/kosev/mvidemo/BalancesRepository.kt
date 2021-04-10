package net.kosev.mvidemo

import kotlinx.coroutines.delay
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalancesRepository @Inject constructor() {

    suspend fun getBalances(): Balances {
        delay(2000)
        return Balances(BigDecimal("1.23450000"), BigDecimal("36456.00"))
    }

}

data class Balances(
    val cryptoBalance: BigDecimal,
    val fiatBalance: BigDecimal
)
