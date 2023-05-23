package ru.dev.fabled.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.dev.fabled.data.mapper.toRepositoryContent
import ru.dev.fabled.data.remote.GitHubApi
import ru.dev.fabled.data.remote.NO_INTERNET_EXCEPTION_CODE
import ru.dev.fabled.data.remote.NetworkExceptionResolver
import ru.dev.fabled.domain.model.RepositoryContent
import ru.dev.fabled.domain.repositories.RepoContentRepository
import timber.log.Timber
import java.net.UnknownHostException

class RepoContentRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val gitHubApi: GitHubApi,
    private val networkExceptionResolver: NetworkExceptionResolver
) : RepoContentRepository {

    override suspend fun getContent(
        repositoryName: String,
        repositoryOwner: String,
        contentPath: String
    ): List<RepositoryContent> = withContext(dispatcher) {
        return@withContext try {
            val response = gitHubApi.getContentFromRepository(
                owner = repositoryOwner,
                repositoryName = repositoryName,
                path = contentPath
            )

            response.toRepositoryContent()
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

}