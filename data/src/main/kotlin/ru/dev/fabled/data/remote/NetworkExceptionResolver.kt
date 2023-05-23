package ru.dev.fabled.data.remote

interface NetworkExceptionResolver {

    fun resolve(errorCode: Int): String

}