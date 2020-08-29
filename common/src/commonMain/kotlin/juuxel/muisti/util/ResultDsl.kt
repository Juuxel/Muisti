package juuxel.muisti.util

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

@RestrictsSuspension
interface ResultSyntax<E> {
    suspend fun <T> Result<T, E>.bind(): T
}

private class ResultContinuation<T, E> : Continuation<Result<T, E>>, ResultSyntax<E> {
    lateinit var result: Result<T, E>

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resumeWith(result: kotlin.Result<Result<T, E>>) {
        result.fold(
            onSuccess = { this.result = it },
            onFailure = { throw it }
        )
    }

    override suspend fun <U> Result<U, E>.bind(): U =
        suspendCoroutineUninterceptedOrReturn {
            when (this) {
                is Ok -> value
                is Err -> {
                    result = this
                    COROUTINE_SUSPENDED
                }
            }
        }
}

fun <T, E> result(block: suspend ResultSyntax<E>.() -> T): Result<T, E> {
    val context = ResultContinuation<T, E>()
    val wrapped: suspend ResultSyntax<E>.() -> Result<T, E> = { Ok(block()) }
    wrapped.startCoroutine(context, context)
    return context.result
}
