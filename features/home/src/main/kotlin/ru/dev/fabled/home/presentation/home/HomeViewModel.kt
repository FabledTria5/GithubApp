package ru.dev.fabled.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.dev.fabled.domain.model.Resource
import ru.dev.fabled.domain.use_cases.SearchGitHub

class HomeViewModel(
    private val searchGitHub: SearchGitHub,
    private val workDispatcher: CoroutineDispatcher
) : ViewModel(), HomeScreenContract {

    private val mutableState = MutableStateFlow(HomeScreenContract.State())
    override val state: StateFlow<HomeScreenContract.State> = mutableState.asStateFlow()

    private val effectsFlow = MutableSharedFlow<HomeScreenContract.Effect>()
    override val effect: SharedFlow<HomeScreenContract.Effect> = effectsFlow

    override fun onEvent(event: HomeScreenContract.Event) = when (event) {
        is HomeScreenContract.Event.ChangeSearchQuery -> changeSearchQuery(event.newQuery)
        HomeScreenContract.Event.Search -> searchData()
    }

    var searchQuery: String = ""

    private fun changeSearchQuery(newQuery: String) {
        val canPerformSearch = newQuery.length >= 3
        searchQuery = newQuery

        if (state.value.canPerformSearch != canPerformSearch) {
            mutableState.update { state ->
                state.copy(canPerformSearch = canPerformSearch)
            }
        }
    }

    private fun searchData() {
        viewModelScope.launch(workDispatcher) {
            require(value = searchQuery.length >= 3) { return@launch }

            effectsFlow.emit(HomeScreenContract.Effect.DismissInput)
            mutableState.update { state -> state.copy(listData = Resource.Loading) }

            when (val result = searchGitHub(searchQuery)) {
                is Resource.Error, is Resource.Success ->
                    mutableState.update { state -> state.copy(listData = result) }

                else -> Unit
            }
        }
    }
}