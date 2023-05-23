package ru.dev.fabled.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.dev.fabled.data.remote.dto.repositories.GitHubRepositoriesResponse
import ru.dev.fabled.data.remote.dto.persons.GitHubPersonsResponse
import ru.dev.fabled.data.remote.dto.repository_content.RepositoryContentResponseItem

const val API_BASE_URL = "https://api.github.com/"

interface GitHubApi {

    @GET(value = "search/users")
    suspend fun searchUsers(
        @Query(value = "q") query: String,
        @Query(value = "page") page: Int = 1,
        @Query(value = "per_page") pageSize: Int = 50
    ): GitHubPersonsResponse

    @GET(value = "search/repositories")
    suspend fun searchRepositories(
        @Query(value = "q") query: String,
        @Query(value = "page") page: Int = 1,
        @Query(value = "per_page") pageSize: Int = 50
    ): GitHubRepositoriesResponse

    @GET(value = "repos/{owner}/{repo}/contents/{path}")
    suspend fun getContentFromRepository(
        @Path(value = "owner") owner: String,
        @Path(value = "repo") repositoryName: String,
        @Path(value = "path", encoded = true) path: String
    ): List<RepositoryContentResponseItem>

}