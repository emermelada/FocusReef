package com.emermeladas.focusreef.data.model

/**
 * One completed focus block, as recorded by the desk on the NAS.
 *
 * This is read-only history: the app never creates or modifies focus blocks.
 *
 * @property id Unique identifier assigned by the NAS database.
 * @property startEpochMillis Start of the block, epoch milliseconds (UTC).
 * @property durationMinutes Length of the block in minutes (normally 25).
 */
data class FocusBlock(
    val id: Long,
    val startEpochMillis: Long,
    val durationMinutes: Int,
)
