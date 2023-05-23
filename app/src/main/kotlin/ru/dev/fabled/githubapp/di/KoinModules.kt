package ru.dev.fabled.githubapp.di

import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.dev.fabled.data.repository.RepoContentRepositoryImpl
import ru.dev.fabled.data.repository.SearchRepositoryImpl
import ru.dev.fabled.domain.repositories.RepoContentRepository
import ru.dev.fabled.domain.repositories.SearchRepository
import ru.dev.fabled.domain.use_cases.GetRepositoryContent
import ru.dev.fabled.domain.use_cases.SearchGitHub
import ru.dev.fabled.home.presentation.home.HomeViewModel
import ru.dev.fabled.repository.repository.RepositoryViewModel

val coroutineDispatcherModule = module {
    single(qualifier = named(name = "IoDispatcher")) {
        Dispatchers.IO
    }

    single(qualifier = named(name = "MainDispatcher")) {
        Dispatchers.Main
    }

    single(qualifier = named(name = "DefaultDispatcher")) {
        Dispatchers.Default
    }

    single(qualifier = named(name = "UnconfinedDispatcher")) {
        Dispatchers.Unconfined
    }
}

val repositoryModule = module {
    single<SearchRepository> {
        SearchRepositoryImpl(
            dispatcher = get(qualifier = named(name = "IoDispatcher")),
            gitHubApi = get(),
            networkExceptionResolver = get()
        )
    }

    single<RepoContentRepository> {
        RepoContentRepositoryImpl(
            dispatcher = get(qualifier = named(name = "IoDispatcher")),
            gitHubApi = get(),
            networkExceptionResolver = get()
        )
    }
}

val useCaseModule = module {
    singleOf(::SearchGitHub)
    singleOf(::GetRepositoryContent)
}

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            searchGitHub = get(),
            workDispatcher = get(qualifier = named(name = "DefaultDispatcher"))
        )
    }

    viewModel {
        RepositoryViewModel(
            savedStateHandle = get(),
            getRepositoryContent = get(),
            workDispatcher = get(qualifier = named(name = "DefaultDispatcher"))
        )
    }
}