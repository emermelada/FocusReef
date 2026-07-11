package com.emermeladas.focusreef.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Wallet math: the balance is always derived, never stored.
 */
class WalletTest {

    @Test
    fun `available tokens is earned minus spent`() {
        assertEquals(7L, Wallet(earnedTokens = 10, spentTokens = 3).availableTokens)
    }

    @Test
    fun `canAfford compares against the available balance not earnings`() {
        val wallet = Wallet(earnedTokens = 10, spentTokens = 8)
        assertTrue(wallet.canAfford(2))
        assertFalse(wallet.canAfford(3))
    }
}
