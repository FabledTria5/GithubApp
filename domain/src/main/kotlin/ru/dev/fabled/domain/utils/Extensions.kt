package ru.dev.fabled.domain.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.EmptyStackException
import java.util.Stack

suspend fun <T, R> List<T>.mapAsync(transformation: suspend (T) -> R): List<R> = coroutineScope {
    this@mapAsync.map { async { transformation(it) } }.awaitAll()
}

fun <T> Stack<T>.peekOrNull(): T? = try {
    peek()
} catch (e: EmptyStackException) {
    null
}