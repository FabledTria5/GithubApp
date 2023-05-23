package ru.dev.fabled.domain.repositories

import ru.dev.fabled.domain.model.SearchData

interface SearchRepository {

    suspend fun searchPersons(searchQuery: String): List<SearchData.PersonModel>

    suspend fun searchRepositories(searchQuery: String): List<SearchData.RepositoryModel>

}