package ru.dev.fabled.data.remote

import android.content.Context
import ru.dev.fabled.data.R

const val NO_INTERNET_EXCEPTION_CODE = 1

class NetworkErrorsResolverImpl(private val context: Context) : NetworkExceptionResolver {

    override fun resolve(errorCode: Int): String {
        return when (errorCode) {
            NO_INTERNET_EXCEPTION_CODE -> context.getString(R.string.no_internet_message)
            in 400..500 -> context.getString(R.string.bad_request_message)
            in 500..600 -> context.getString(R.string.server_error_message)
            else -> context.getString(R.string.unknown_network_exception)
        }
    }

}