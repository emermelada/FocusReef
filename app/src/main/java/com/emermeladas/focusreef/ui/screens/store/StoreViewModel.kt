package com.emermeladas.focusreef.ui.screens.store

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.DecorationPurchaseResult
import com.emermeladas.focusreef.data.model.DecorationSpecies
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.PurchaseResult
import com.emermeladas.focusreef.data.repositories.AquariumRepository
import com.emermeladas.focusreef.data.repositories.ProgressionRepository
import com.emermeladas.focusreef.data.repositories.WalletRepository
import com.emermeladas.focusreef.utils.BiasPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Store logic: exposes the wallet + tanks as UI state and executes purchases.
 *
 * Purchase outcomes surface as a one-shot message ([userMessageRes]) that the
 * screen shows in a snackbar and then acknowledges via [onMessageShown].
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StoreViewModel @Inject constructor(
    private val aquariumRepository: AquariumRepository,
    walletRepository: WalletRepository,
    progressionRepository: ProgressionRepository,
) : ViewModel() {

    /** Bumped by [retry]; each new value re-subscribes to the sources. */
    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<StoreUiState> = retryTrigger
        .flatMapLatest {
            combine(
                walletRepository.observeWallet(),
                aquariumRepository.observeTanks(),
                progressionRepository.observeProgression(),
            ) { wallet, tanks, progression ->
                StoreUiState(
                    isLoading = false,
                    wallet = wallet,
                    tanks = tanks,
                    progression = progression,
                )
            }
        }
        // The balance is derived from NAS history, so the store can fail to
        // load for exactly the same reason Stats can.
        .catch {
            emit(
                StoreUiState(
                    isLoading = false,
                    errorRes = R.string.error_history_unavailable,
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StoreUiState(),
        )

    /** Re-runs the failed load after the player taps "Try again". */
    fun retry() {
        retryTrigger.update { it + 1 }
    }

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

    private val _pendingPlacement = MutableStateFlow<PendingPlacement?>(null)

    /** A just-bought decoration waiting for the player to drag it into place. */
    val pendingPlacement: StateFlow<PendingPlacement?> = _pendingPlacement.asStateFlow()

    /**
     * Buys a decoration into [tankId]; on success the screen opens placement
     * mode via [pendingPlacement].
     */
    fun buyDecoration(species: DecorationSpecies, tankId: Long) {
        viewModelScope.launch {
            when (val result = aquariumRepository.buyDecoration(species, tankId)) {
                is DecorationPurchaseResult.Success -> _pendingPlacement.value =
                    PendingPlacement(result.decorationId, tankId, species)
                DecorationPurchaseResult.NotEnoughTokens ->
                    _userMessageRes.value = R.string.store_msg_not_enough_tokens
                DecorationPurchaseResult.TankFull ->
                    _userMessageRes.value = R.string.store_msg_decoration_limit
                is DecorationPurchaseResult.LevelTooLow ->
                    _userMessageRes.value = R.string.store_msg_level_too_low
            }
        }
    }

    /** Persists the dragged position of the pending decoration. */
    fun confirmPlacement(position: BiasPoint) {
        val pending = _pendingPlacement.value ?: return
        viewModelScope.launch {
            aquariumRepository.updateDecorationPosition(
                decorationId = pending.decorationId,
                xBias = position.x,
                yBias = position.y,
            )
            _pendingPlacement.value = null
        }
    }

    /** Closes placement mode keeping the default spot (purchase already committed). */
    fun dismissPlacement() {
        _pendingPlacement.value = null
    }

    @StringRes
    private fun PurchaseResult.toMessageRes(): Int = when (this) {
        PurchaseResult.Success -> R.string.store_msg_purchased
        PurchaseResult.NotEnoughTokens -> R.string.store_msg_not_enough_tokens
        PurchaseResult.NotEnoughSpace -> R.string.store_msg_no_space
        is PurchaseResult.LevelTooLow -> R.string.store_msg_level_too_low
    }
}

/** A decoration bought but not yet dragged into its final spot. */
data class PendingPlacement(
    val decorationId: Long,
    val tankId: Long,
    val species: DecorationSpecies,
)
