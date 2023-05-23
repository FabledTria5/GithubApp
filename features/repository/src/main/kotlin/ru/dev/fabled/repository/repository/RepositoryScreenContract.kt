package ru.dev.fabled.repository.repository

import ru.dev.fabled.core.ScreenContract
import ru.dev.fabled.domain.model.RepositoryContent
import ru.dev.fabled.domain.model.Resource

interface RepositoryScreenContract :
    ScreenContract<RepositoryScreenContract.State, RepositoryScreenContract.Event, RepositoryScreenContract.Effect> {

    data class State(
        val canNavigateUp: Boolean = true,
        val isWebEnabled: Boolean = false,
        val currentContent: Resource<List<RepositoryContent>> = Resource.Idle
    )

    sealed class Event {
        data class NavigateToPath(val path: String) : Event()
        data class OpenFile(val fileUrl: String) : Event()
        object NavigateBack : Event()
        object Retry : Event()
    }

    sealed class Effect {
        data class OpenFile(val fileUrl: String) : Effect()
    }

}