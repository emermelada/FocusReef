package com.emermeladas.focusreef.data.remote.dto

import com.emermeladas.focusreef.data.model.FocusBlock
import com.google.gson.annotations.SerializedName

/**
 * Wire format of a focus block as served by the NAS REST API.
 *
 * Kept separate from the domain [FocusBlock] so the API can evolve without
 * touching the rest of the app.
 */
data class FocusBlockDto(
    @SerializedName("id") val id: Long,
    @SerializedName("start_epoch_millis") val startEpochMillis: Long,
    @SerializedName("duration_minutes") val durationMinutes: Int,
) {
    /** Maps this DTO to the domain model used by the rest of the app. */
    fun toDomain(): FocusBlock = FocusBlock(
        id = id,
        startEpochMillis = startEpochMillis,
        durationMinutes = durationMinutes,
    )
}
