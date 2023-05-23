package ru.dev.fabled.data.mapper

import ru.dev.fabled.domain.model.RepositoryContent
import ru.dev.fabled.domain.model.SearchData
import ru.dev.fabled.domain.utils.mapAsync
import ru.dev.fabled.data.remote.dto.repositories.GitHubRepositoriesResponse
import ru.dev.fabled.data.remote.dto.persons.GitHubPersonsResponse
import ru.dev.fabled.data.remote.dto.repository_content.RepositoryContentResponseItem

suspend fun GitHubPersonsResponse.toPersonsList() = items.mapAsync { item ->
    SearchData.PersonModel(
        userId = item.id,
        userLogin = item.login,
        avatarUrl = item.avatarUrl,
        score = item.score.toFloat()
    )
}

suspend fun GitHubRepositoriesResponse.toRepositoriesList() = items.mapAsync { item ->
    SearchData.RepositoryModel(
        repositoryId = item.id,
        repositoryName = item.name,
        forksCount = item.forksCount,
        description = item.description,
        repositoryOwner = item.owner.login
    )
}

suspend fun List<RepositoryContentResponseItem>.toRepositoryContent(): List<RepositoryContent> =
    mapAsync { item ->
        when (item.type) {
            "file" -> RepositoryContent.File(
                fileName = item.name,
                fileUrl = item.downloadUrl ?: item.url
            )

            else -> RepositoryContent.Folder(folderName = item.name, path = item.path)
        }
    }