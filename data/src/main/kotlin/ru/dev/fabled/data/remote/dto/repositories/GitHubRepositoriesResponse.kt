package ru.dev.fabled.data.remote.dto.repositories

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class GitHubRepositoriesResponse(
    @SerialName("incomplete_results")
    val incompleteResults: Boolean,
    @SerialName("items")
    val items: List<Item>,
    @SerialName("total_count")
    val totalCount: Int
)