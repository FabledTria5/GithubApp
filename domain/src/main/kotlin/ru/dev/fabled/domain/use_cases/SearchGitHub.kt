package ru.dev.fabled.domain.use_cases

import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import ru.dev.fabled.domain.model.Resource
import ru.dev.fabled.domain.model.SearchData
import ru.dev.fabled.domain.repositories.SearchRepository

class SearchGitHub(private val searchRepository: SearchRepository) {

    suspend operator fun invoke(searchQuery: String) = coroutineScope {
        // Job() uses to handle exceptions from async blocks
        val personsJob = async(Job()) { searchRepository.searchPersons(searchQuery) }
        val repositoriesJob = async(Job()) { searchRepository.searchRepositories(searchQuery) }

        return@coroutineScope try {
            val personsResult = personsJob.await()
            val repositoriesResult = repositoriesJob.await()

            val resultList = (personsResult + repositoriesResult)
                .sortedBy { searchData ->
                    when (searchData) {
                        is SearchData.RepositoryModel -> searchData.description
                        is SearchData.PersonModel -> searchData.userLogin
                    }
                }

            Resource.Success(data = resultList)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

}