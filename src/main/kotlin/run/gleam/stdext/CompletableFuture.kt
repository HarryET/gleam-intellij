package run.gleam.stdext

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Future

fun <T> supplyAsync(executor: Executor, supplier: () -> T): CompletableFuture<T> =
    CompletableFuture.supplyAsync({ supplier() }, executor)

fun <T> Future<T>.getWithRethrow(): T =
    try {
        get()
    } catch (e: ExecutionException) {
        throw e.cause ?: e
    }