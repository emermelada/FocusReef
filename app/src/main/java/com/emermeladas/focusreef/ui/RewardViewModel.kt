package com.emermeladas.focusreef.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermeladas.focusreef.data.local.ReefPreferences
import com.emermeladas.focusreef.data.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Drives the welcome-back earnings moment: on app start, compares the
 * wallet's earned-token total against the last total the user was shown and
 * surfaces the difference once, calmly, before it just sits in the balance.
 *
 * The new baseline is persisted immediately (not on dismissal), so a killed
 * process never re-celebrates the same tokens.
 */
@HiltViewModel
class RewardViewModel @Inject constructor(
    walletRepository: WalletRepository,
    private val preferences: ReefPreferences,
) : ViewModel() {

    private val _newlyEarnedTokens = MutableStateFlow(0L)

    /** Tokens earned since the user last opened the app; 0 = nothing to show. */
    val newlyEarnedTokens: StateFlow<Long> = _newlyEarnedTokens.asStateFlow()

    init {
        viewModelScope.launch {
            val earned = walletRepository.observeWallet().first().earnedTokens
            val lastSeen = preferences.lastSeenEarnedTokens.first()
            when {
                // First launch: store a baseline silently — congratulating
                // the user for their entire history would be noise.
                lastSeen == null -> preferences.setLastSeenEarnedTokens(earned)

                earned > lastSeen -> {
                    _newlyEarnedTokens.value = earned - lastSeen
                    preferences.setLastSeenEarnedTokens(earned)
                }
            }
        }
    }

    /** The moment finished (timeout or tap) — clear it. */
    fun onEarningsMomentDone() {
        _newlyEarnedTokens.value = 0L
    }
}
