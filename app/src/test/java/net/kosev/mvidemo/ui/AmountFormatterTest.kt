package net.kosev.mvidemo.ui

import net.kosev.mvidemo.ui.AmountFormatter
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class AmountFormatterTest {

    private val tested = AmountFormatter()

    @Test
    fun `formatCrypto with less fraction digits should format with 8 fraction digits`() =
        assertEquals("1.23000000", tested.formatCrypto(BigDecimal("1.23")))

    @Test
    fun `formatCrypto with more fraction digits should format with 8 fraction digits`() =
        assertEquals("1.23456789", tested.formatCrypto(BigDecimal("1.234567891234")))

    @Test
    fun `formatCryptoWithSymbol should prepend the BTC symbol`() =
        assertEquals("BTC 1.00000000", tested.formatCryptoWithSymbol(BigDecimal.ONE))

    @Test
    fun `formatFiat with less fraction digits should format with 2 fraction digits`() =
        assertEquals("1.20", tested.formatFiat(BigDecimal("1.2")))

    @Test
    fun `formatFiat with more fraction digits should format with 2 fraction digits`() =
        assertEquals("1.24", tested.formatFiat(BigDecimal("1.236")))

    @Test
    fun `formatFiatWithSymbol should prepend the EUR symbol`() =
        assertEquals("EUR 1.00", tested.formatFiatWithSymbol(BigDecimal.ONE))

    @Test
    fun `formatExchangeRate should return correct format`() =
        assertEquals("BTC 1 = EUR 10.00", tested.formatExchangeRate(BigDecimal.TEN))

}
