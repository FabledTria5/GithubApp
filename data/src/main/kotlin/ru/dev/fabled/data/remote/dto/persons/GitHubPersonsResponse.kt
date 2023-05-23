package ru.dev.fabled.data.remote.dto.persons

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class GitHubPersonsResponse(
    @SerialName("incomplete_results")
    val incompleteResults: Boolean,
    @SerialName("items")
    val items: List<Item>,
    @SerialName("total_count")
    val totalCount: Int
)