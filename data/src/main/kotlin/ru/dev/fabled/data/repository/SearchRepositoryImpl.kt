package ru.dev.fabled.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.dev.fabled.domain.model.SearchData
import ru.dev.fabled.domain.repositories.SearchRepository
import ru.dev.fabled.data.mapper.toPersonsList
import ru.dev.fabled.data.mapper.toRepositoriesList
import ru.dev.fabled.data.remote.GitHubApi
import ru.dev.fabled.data.remote.NO_INTERNET_EXCEPTION_CODE
import ru.dev.fabled.data.remote.NetworkExceptionResolver
import timber.log.Timber
import java.net.UnknownHostException

class SearchRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val gitHubApi: GitHubApi,
    private val networkExceptionResolver: NetworkExceptionResolver
) : SearchRepository {

    override suspend fun searchPersons(searchQuery: String): List<SearchData.PersonModel> =
        withContext(dispatcher) {
            return@withContext try {
                val response = gitHubApi.searchUsers(query = searchQuery)

                response.toPersonsList()
            } catch (e: UnknownHostException) { // This occurs when there is no Internet connection
                Timber.e(e)
                throw RuntimeException(networkExceptionResolver.resolve(NO_INTERNET_EXCEPTION_CODE))
            } catch (e: HttpException) {
                Timber.e(e)
                throw RuntimeException(networkExceptionResolver.resolve(e.code()))
            } catch (e: Exception) {
                Timber.e(e)
                throw RuntimeException("Unknown error")
            }
        }

    override suspend fun searchRepositories(searchQuery: String): List<SearchData.RepositoryModel> =
        withContext(dispatcher) {
            return@withContext try {
                val response = gitHubApi.searchRepositories(query = searchQuery)

                response.toRepositoriesList()
            } catch (e: UnknownHostException) { // This occurs when there is no Internet connection
                Timber.e(e)
                throw RuntimeException(networkExceptionResolver.resolve(NO_INTERNET_EXCEPTION_CODE))
            } catch (e: HttpException) {
                Timber.e(e)
                throw RuntimeException(networkExceptionResolver.resolve(e.code()))
            }
            catch (e: Exception) {
                Timber.e(e)
                throw RuntimeException("Unknown error")
            }
        }

}
