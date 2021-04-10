package net.kosev.mvidemo

import kotlinx.coroutines.delay
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RateRepository @Inject constructor() {

    suspend fun getCryptoRate(): BigDecimal {
        delay(500)
        return HARDCODED_BITCOIN_PRICE
    }

    companion object {
        private val HARDCODED_BITCOIN_PRICE = BigDecimal("50268.47")
    }

}
