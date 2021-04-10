package net.kosev.mvidemo.ui.trade

import java.math.BigDecimal
import java.text.NumberFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AmountFormatter @Inject constructor() {

    fun formatCrypto(amount: BigDecimal): String =
        NumberFormat.getInstance().apply {
            minimumFractionDigits = CRYPTO_FRACTION_DIGITS
            maximumFractionDigits = CRYPTO_FRACTION_DIGITS
        }.format(amount)

    fun formatCryptoWithSymbol(amount: BigDecimal): String =
        "BTC ${formatCrypto(amount)}"

    fun formatFiat(amount: BigDecimal): String =
        NumberFormat.getInstance().apply {
            minimumFractionDigits = FIAT_FRACTION_DIGITS
            maximumFractionDigits = FIAT_FRACTION_DIGITS
        }.format(amount)

    fun formatFiatWithSymbol(amount: BigDecimal): String =
        "EUR ${formatFiat(amount)}"

    fun formatExchangeRate(cryptoPrice: BigDecimal): String =
        "1 BTC = ${formatFiat(cryptoPrice)}"

    companion object {
        private const val CRYPTO_FRACTION_DIGITS = 8
        private const val FIAT_FRACTION_DIGITS = 2
    }

}
