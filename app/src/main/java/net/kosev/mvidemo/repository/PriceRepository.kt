package net.kosev.mvidemo.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRepository(private val defaultDispatcher: CoroutineDispatcher) {

    @Inject
    constructor() : this(Dispatchers.IO)

    suspend fun getCryptoPrice(): BigDecimal = withContext(defaultDispatcher) {
        delay(500)
        HARDCODED_BITCOIN_PRICE
    }

    companion object {
        private val HARDCODED_BITCOIN_PRICE = BigDecimal("50268.47")
    }

}
