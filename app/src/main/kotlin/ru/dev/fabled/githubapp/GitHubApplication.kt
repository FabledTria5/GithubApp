package ru.dev.fabled.githubapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.dev.fabled.data.di.networkModule
import ru.dev.fabled.githubapp.di.coroutineDispatcherModule
import ru.dev.fabled.githubapp.di.repositoryModule
import ru.dev.fabled.githubapp.di.useCaseModule
import ru.dev.fabled.githubapp.di.viewModelModule
import timber.log.Timber

class GitHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(applicationContext)

            modules(
                coroutineDispatcherModule,
                viewModelModule,
                repositoryModule,
                networkModule,
                useCaseModule
            )
        }
    }

}