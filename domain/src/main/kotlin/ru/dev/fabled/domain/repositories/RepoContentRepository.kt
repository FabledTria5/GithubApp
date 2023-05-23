package ru.dev.fabled.domain.repositories

import ru.dev.fabled.domain.model.RepositoryContent

interface RepoContentRepository {

    suspend fun getContent(
        repositoryName: String,
        repositoryOwner: String,
        contentPath: String
    ): List<RepositoryContent>

}