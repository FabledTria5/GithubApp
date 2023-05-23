package ru.dev.fabled.home.presentation.home

import ru.dev.fabled.core.ScreenContract
import ru.dev.fabled.domain.model.Resource
import ru.dev.fabled.domain.model.SearchData

interface HomeScreenContract :
    ScreenContract<HomeScreenContract.State, HomeScreenContract.Event, HomeScreenContract.Effect> {

    data class State(
        val canPerformSearch: Boolean = false,
        val listData: Resource<List<SearchData>> = Resource.Idle
    )

    sealed class Event {
        data class ChangeSearchQuery(val newQuery: String) : Event()
        object Search : Event()
    }

    sealed class Effect {
        object DismissInput : Effect()
    }

}