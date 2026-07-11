package com.emermeladas.focusreef.data.repositories

import com.emermeladas.focusreef.data.local.daos.PurchaseDao
import com.emermeladas.focusreef.data.model.Wallet
import com.emermeladas.focusreef.utils.GameConfig
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Derives the player's token balance.
 *
 * Earned = focus blocks on the NAS × [GameConfig.TOKENS_PER_FOCUS_BLOCK];
 * spent = purchase ledger in Room. The balance is never stored anywhere.
 */
interface WalletRepository {

    /** The current wallet as a reactive stream; updates when either side changes. */
    fun observeWallet(): Flow<Wallet>
}

/**
 * Default [WalletRepository]: combines the focus history with the local
 * purchase ledger.
 */
@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val focusHistoryRepository: FocusHistoryRepository,
    private val purchaseDao: PurchaseDao,
) : WalletRepository {

    override fun observeWallet(): Flow<Wallet> = combine(
        focusHistoryRepository.observeFocusBlocks(),
        purchaseDao.observeTotalSpent(),
    ) { blocks, spent ->
        Wallet(
            earnedTokens = blocks.size * GameConfig.TOKENS_PER_FOCUS_BLOCK,
            spentTokens = spent,
        )
    }
}
