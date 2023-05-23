package ru.dev.fabled.domain.use_cases

import ru.dev.fabled.domain.model.RepositoryContent
import ru.dev.fabled.domain.repositories.RepoContentRepository

class GetRepositoryContent(private val repository: RepoContentRepository) {

    suspend operator fun invoke(
        repositoryName: String,
        repositoryOwner: String,
        contentPath: String
    ) = repository
        .getContent(repositoryName, repositoryOwner, contentPath)
        .sortedBy { content -> content is RepositoryContent.File }

}