package ru.dev.fabled.data.remote.dto.repository_content

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class RepositoryContentResponseItem(
    @SerialName("download_url")
    val downloadUrl: String?,
    @SerialName("git_url")
    val gitUrl: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("path")
    val path: String,
    @SerialName("sha")
    val sha: String,
    @SerialName("size")
    val size: Int,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String
)