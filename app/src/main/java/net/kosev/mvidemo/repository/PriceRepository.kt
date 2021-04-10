package net.kosev.mvidemo.repository

import kotlinx.coroutines.delay
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRepository @Inject constructor() {

    suspend fun getCryptoPrice(): BigDecimal {
        delay(500)
        return HARDCODED_BITCOIN_PRICE
    }

    companion object {
        private val HARDCODED_BITCOIN_PRICE = BigDecimal("50268.47")
    }

}
