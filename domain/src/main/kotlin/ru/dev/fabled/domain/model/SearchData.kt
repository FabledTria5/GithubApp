package ru.dev.fabled.domain.model

sealed class SearchData {

    data class RepositoryModel(
        val repositoryId: Int,
        val repositoryName: String,
        val forksCount: Int,
        val description: String?,
        val repositoryOwner: String
    ): SearchData()

    data class PersonModel(
        val userId: Int,
        val userLogin: String,
        val avatarUrl: String?,
        val score: Float
    ): SearchData()

}