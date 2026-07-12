package com.emermeladas.focusreef.ui.screens.store

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.PurchaseResult
import com.emermeladas.focusreef.data.repositories.AquariumRepository
import com.emermeladas.focusreef.data.repositories.ProgressionRepository
import com.emermeladas.focusreef.data.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Store logic: exposes the wallet + tanks as UI state and executes purchases.
 *
 * Purchase outcomes surface as a one-shot message ([userMessageRes]) that the
 * screen shows in a snackbar and then acknowledges via [onMessageShown].
 */
@HiltViewModel
class StoreViewModel @Inject constructor(
    private val aquariumRepository: AquariumRepository,
    walletRepository: WalletRepository,
    progressionRepository: ProgressionRepository,
) : ViewModel() {

    val uiState: StateFlow<StoreUiState> = combine(
        walletRepository.observeWallet(),
        aquariumRepository.observeTanks(),
        progressionRepository.observeProgression(),
    ) { wallet, tanks, progression ->
        StoreUiState(isLoading = false, wallet = wallet, tanks = tanks, progression = progression)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StoreUiState(),
    )

    private val _userMessageRes = MutableStateFlow<Int?>(null)

    /** String resource of a pending purchase-feedback message, or null. */
    @get:StringRes
    val userMessageRes: StateFlow<Int?> = _userMessageRes.asStateFlow()

    /** Attempts to buy a fish of [species] and place it in tank [tankId]. */
    fun buyFish(species: FishSpecies, tankId: Long) {
        viewModelScope.launch {
            _userMessageRes.value = aquariumRepository.buyFish(species, tankId).toMessageRes()
        }
    }

    /** Attempts to buy an additional tank. */
    fun buyTank() {
        viewModelScope.launch {
            _userMessageRes.value = aquariumRepository.buyTank().toMessageRes()
        }
    }

    /** Called by the screen once the message has been displayed. */
    fun onMessageShown() {
        _userMessageRes.value = null
    }

    @StringRes
    private fun PurchaseResult.toMessageRes(): Int = when (this) {
        PurchaseResult.Success -> R.string.store_msg_purchased
        PurchaseResult.NotEnoughTokens -> R.string.store_msg_not_enough_tokens
        PurchaseResult.NotEnoughSpace -> R.string.store_msg_no_space
        is PurchaseResult.LevelTooLow -> R.string.store_msg_level_too_low
    }
}
