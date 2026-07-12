package com.emermeladas.focusreef.data.repositories

import androidx.room.withTransaction
import com.emermeladas.focusreef.data.local.FocusReefDatabase
import com.emermeladas.focusreef.data.local.daos.AquariumDao
import com.emermeladas.focusreef.data.local.daos.PurchaseDao
import com.emermeladas.focusreef.data.local.entities.DecorationEntity
import com.emermeladas.focusreef.data.local.entities.FishEntity
import com.emermeladas.focusreef.data.local.entities.PurchaseEntity
import com.emermeladas.focusreef.data.local.entities.TankEntity
import com.emermeladas.focusreef.data.model.Decoration
import com.emermeladas.focusreef.data.model.DecorationPurchaseResult
import com.emermeladas.focusreef.data.model.DecorationSpecies
import com.emermeladas.focusreef.data.model.Fish
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.MoveResult
import com.emermeladas.focusreef.data.model.PurchaseResult
import com.emermeladas.focusreef.data.model.Tank
import com.emermeladas.focusreef.utils.BiasPoint
import com.emermeladas.focusreef.utils.GameConfig
import com.emermeladas.focusreef.utils.PlacementMath
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

/**
 * Owns the aquarium game state: tanks, fish, and store purchases.
 *
 * Purchases are validated here (balance + tank space) and persisted
 * atomically (item + ledger entry in one transaction), so callers can simply
 * map the returned [PurchaseResult] to UI feedback.
 */
interface AquariumRepository {

    /** All tanks with their fish, as a reactive stream. */
    fun observeTanks(): Flow<List<Tank>>

    /** Buys a fish of [species] and places it in the tank chosen by the player. */
    suspend fun buyFish(species: FishSpecies, tankId: Long): PurchaseResult

    /** Buys an additional empty tank. */
    suspend fun buyTank(): PurchaseResult

    /** Moves one fish of [species] from one tank to another. */
    suspend fun moveFish(species: FishSpecies, fromTankId: Long, toTankId: Long): MoveResult

    /**
     * Buys a decoration of [species] into [tankId] at its default position.
     * The purchase commits immediately; placement mode only adjusts position.
     */
    suspend fun buyDecoration(species: DecorationSpecies, tankId: Long): DecorationPurchaseResult

    /** Moves a placed decoration to a new position inside its tank. */
    suspend fun updateDecorationPosition(decorationId: Long, xBias: Float, yBias: Float)
}

/**
 * Default [AquariumRepository] backed by Room.
 */
@Singleton
class AquariumRepositoryImpl @Inject constructor(
    private val database: FocusReefDatabase,
    private val aquariumDao: AquariumDao,
    private val purchaseDao: PurchaseDao,
    private val walletRepository: WalletRepository,
    private val progressionRepository: ProgressionRepository,
) : AquariumRepository {

    override fun observeTanks(): Flow<List<Tank>> = combine(
        aquariumDao.observeTanks(),
        aquariumDao.observeFish(),
        aquariumDao.observeDecorations(),
    ) { tankEntities, fishEntities, decorationEntities ->
        tankEntities.map { tank ->
            Tank(
                id = tank.id,
                name = tank.name,
                capacitySlots = GameConfig.TANK_CAPACITY_SLOTS,
                fish = fishEntities
                    .filter { it.tankId == tank.id }
                    .map { it.toDomain() },
                decorations = decorationEntities
                    .filter { it.tankId == tank.id }
                    .map { it.toDomain() },
            )
        }
    }

    override suspend fun buyFish(species: FishSpecies, tankId: Long): PurchaseResult {
        // Defense in depth: the store disables locked cards, but the gate lives here.
        val progression = progressionRepository.observeProgression().first()
        if (progression.level < species.unlockLevel) {
            return PurchaseResult.LevelTooLow(species.unlockLevel)
        }

        val wallet = walletRepository.observeWallet().first()
        if (!wallet.canAfford(species.priceTokens)) return PurchaseResult.NotEnoughTokens

        val targetTank = observeTanks().first()
            .firstOrNull { it.id == tankId && it.hasRoomFor(species) }
            ?: return PurchaseResult.NotEnoughSpace

        // Fish + ledger entry must land together, or tokens could be lost.
        database.withTransaction {
            aquariumDao.insertFish(FishEntity(speciesName = species.name, tankId = targetTank.id))
            purchaseDao.insert(purchaseOf(species.displayName, species.priceTokens))
        }
        return PurchaseResult.Success
    }

    override suspend fun buyTank(): PurchaseResult {
        val wallet = walletRepository.observeWallet().first()
        if (!wallet.canAfford(GameConfig.TANK_PRICE_TOKENS)) return PurchaseResult.NotEnoughTokens

        val tankNumber = observeTanks().first().size + 1
        database.withTransaction {
            aquariumDao.insertTank(TankEntity(name = "Tank $tankNumber"))
            purchaseDao.insert(purchaseOf("Tank $tankNumber", GameConfig.TANK_PRICE_TOKENS))
        }
        return PurchaseResult.Success
    }

    override suspend fun moveFish(
        species: FishSpecies,
        fromTankId: Long,
        toTankId: Long,
    ): MoveResult {
        if (fromTankId == toTankId) return MoveResult.Success

        val destination = observeTanks().first().firstOrNull { it.id == toTankId }
        if (destination == null || !destination.hasRoomFor(species)) {
            return MoveResult.NotEnoughSpace
        }

        val fish = aquariumDao.findFishInTank(fromTankId, species.name)
            ?: return MoveResult.NothingToMove
        aquariumDao.updateFishTank(fishId = fish.id, toTankId = toTankId)
        return MoveResult.Success
    }

    override suspend fun buyDecoration(
        species: DecorationSpecies,
        tankId: Long,
    ): DecorationPurchaseResult {
        // Defense in depth: the store disables locked cards, but the gate lives here.
        val progression = progressionRepository.observeProgression().first()
        if (progression.level < species.unlockLevel) {
            return DecorationPurchaseResult.LevelTooLow(species.unlockLevel)
        }

        val wallet = walletRepository.observeWallet().first()
        if (!wallet.canAfford(species.priceTokens)) {
            return DecorationPurchaseResult.NotEnoughTokens
        }

        val targetTank = observeTanks().first()
            .firstOrNull { it.id == tankId && it.hasRoomForDecoration() }
            ?: return DecorationPurchaseResult.TankFull

        val defaultPosition = PlacementMath.defaultPosition(species.placement)
        // Decoration + ledger entry must land together, or tokens could be lost.
        var decorationId = 0L
        database.withTransaction {
            decorationId = aquariumDao.insertDecoration(
                DecorationEntity(
                    speciesName = species.name,
                    tankId = targetTank.id,
                    xBias = defaultPosition.x,
                    yBias = defaultPosition.y,
                ),
            )
            purchaseDao.insert(purchaseOf(species.displayName, species.priceTokens))
        }
        return DecorationPurchaseResult.Success(decorationId)
    }

    override suspend fun updateDecorationPosition(
        decorationId: Long,
        xBias: Float,
        yBias: Float,
    ) {
        // The UI clamps already; re-clamp here so bad values can never persist.
        val entity = aquariumDao.observeDecorations().first()
            .firstOrNull { it.id == decorationId } ?: return
        val species = DecorationSpecies.valueOf(entity.speciesName)
        val clamped = PlacementMath.clamp(BiasPoint(xBias, yBias), species.placement)
        aquariumDao.updateDecorationPosition(decorationId, clamped.x, clamped.y)
    }

    /** Builds a ledger entry timestamped now. */
    private fun purchaseOf(description: String, priceTokens: Long) = PurchaseEntity(
        description = description,
        priceTokens = priceTokens,
        epochMillis = System.currentTimeMillis(),
    )

    /** Maps a Room fish row to the domain model. */
    private fun FishEntity.toDomain() = Fish(
        id = id,
        species = FishSpecies.valueOf(speciesName),
        tankId = tankId,
    )

    /** Maps a Room decoration row to the domain model. */
    private fun DecorationEntity.toDomain() = Decoration(
        id = id,
        species = DecorationSpecies.valueOf(speciesName),
        tankId = tankId,
        xBias = xBias,
        yBias = yBias,
    )
}
