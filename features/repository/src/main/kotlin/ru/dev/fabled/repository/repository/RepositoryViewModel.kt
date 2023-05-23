package ru.dev.fabled.repository.repository

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.dev.fabled.domain.model.RepositoryContent
import ru.dev.fabled.domain.model.Resource
import ru.dev.fabled.domain.use_cases.GetRepositoryContent
import timber.log.Timber

class RepositoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val getRepositoryContent: GetRepositoryContent,
    private val workDispatcher: CoroutineDispatcher
) : ViewModel(), RepositoryScreenContract {

    private val mutableState = MutableStateFlow(RepositoryScreenContract.State())
    override val state: StateFlow<RepositoryScreenContract.State> = mutableState.asStateFlow()

    private val effectsFlow = MutableSharedFlow<RepositoryScreenContract.Effect>()
    override val effect: SharedFlow<RepositoryScreenContract.Effect> = effectsFlow.asSharedFlow()

    private val contentStack: ArrayDeque<Resource<List<RepositoryContent>>> = ArrayDeque()

    private val repositoryName: String by lazy {
        RepositoryFragmentArgs.fromSavedStateHandle(savedStateHandle).repositoryName
    }

    private val repositoryOwner: String by lazy {
        RepositoryFragmentArgs.fromSavedStateHandle(savedStateHandle).repositoryOwner
    }

    private var currentPath: String = ""

    init {
        navigateToPath(path = currentPath)
    }

    override fun onEvent(event: RepositoryScreenContract.Event) = when (event) {
        RepositoryScreenContract.Event.NavigateBack -> navigateBack()
        RepositoryScreenContract.Event.Retry -> navigateToPath(currentPath)
        is RepositoryScreenContract.Event.NavigateToPath -> navigateToPath(event.path)
        is RepositoryScreenContract.Event.OpenFile -> openFile(event.fileUrl)
    }

    private fun navigateBack() {
        mutableState.update { state ->
            if (state.isWebEnabled) {
                state.copy(
                    isWebEnabled = false,
                    canNavigateUp = contentStack.isNotEmpty()
                )
            } else {
                state.copy(
                    currentContent = contentStack.removeLast(),
                    canNavigateUp = contentStack.isNotEmpty()
                )
            }
        }
    }

    private fun navigateToPath(path: String) {
        viewModelScope.launch(workDispatcher) {
            currentPath = path

            val currentNode = state.value.currentContent
            mutableState.update { state -> state.copy(currentContent = Resource.Loading) }

            Timber.d(currentNode.toString())

            if (currentNode is Resource.Success)
                contentStack += currentNode

            try {
                val result = Resource.Success(
                    data = getRepositoryContent(
                        repositoryName = repositoryName,
                        repositoryOwner = repositoryOwner,
                        contentPath = path
                    )
                )

                currentPath = ""

                mutableState.update { state ->
                    state.copy(
                        canNavigateUp = contentStack.isNotEmpty(),
                        currentContent = result
                    )
                }
            } catch (e: Throwable) {
                mutableState.update { state ->
                    state.copy(currentContent = Resource.Error(e))
                }
            }
        }
    }

    private fun openFile(fileUrl: String) {
        viewModelScope.launch(workDispatcher) {
            mutableState.update { state -> state.copy(canNavigateUp = true, isWebEnabled = true) }

            effectsFlow.emit(RepositoryScreenContract.Effect.OpenFile(fileUrl))
        }
    }
}