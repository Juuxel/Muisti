package juuxel.muisti.util

/** A value that can be an [Ok] containing [T] or an [Err] containing [E]. */
sealed class Result<out T, out E> {
    /** Maps this result with [transform] if [Ok]. */
    inline fun <U> map(transform: (T) -> U): Result<U, E> =
        when (this) {
            is Ok -> Ok(transform(value))
            is Err -> this
        }

    /** Maps and flattens this result with [transform] if [Ok]. */
    inline fun <U> flatMap(transform: (T) -> Result<U, @UnsafeVariance E>): Result<U, E> =
        when (this) {
            is Ok -> transform(value)
            is Err -> this
        }

    /**
     * Folds this result into an [R] using the [ok] and [err] mappers.
     */
    inline fun <R> fold(ok: (T) -> R, err: (E) -> R): R =
        when (this) {
            is Ok -> ok(value)
            is Err -> err(error)
        }

    /**
     * Returns the value of this result if [Ok], otherwise converts the error using [err].
     */
    inline fun orElse(err: (E) -> @UnsafeVariance T): T = fold(ok = { it }, err)

    /**
     * Returns the value of [fn] if [Ok], otherwise returns `null`.
     */
    inline fun <R> ifOk(fn: (T) -> R): R? =
        when (this) {
            is Ok -> fn(value)
            is Err -> null
        }

    companion object {
        /** Runs the [block] and catches exceptions in [Err] values. */
        inline fun <A> catching(block: () -> A): Result<A, Exception> =
            try {
                Ok(block())
            } catch (e: Exception) {
                Err(e)
            }
    }
}

/**
 * A successful result containing a [T] [value].
 */
data class Ok<out T>(val value: T) : Result<T, Nothing>()

/**
 * An unsuccessful result containing an [E] [error].
 */
data class Err<out E>(val error: E) : Result<Nothing, E>()

fun <T, E> Result<Result<T, E>, E>.flatten(): Result<T, E> =
    when (this) {
        is Ok -> value
        is Err -> this
    }

fun <T, E> Sequence<Result<T, E>>.lift(): Result<Sequence<T>, E> {
    val buffer = ArrayList<T>()

    for (res in this) {
        when (res) {
            is Ok -> buffer.add(res.value)
            is Err -> return res
        }
    }

    return Ok(buffer.asSequence())
}

fun <A, B, E> Pair<Result<A, E>, B>.liftA(): Result<Pair<A, B>, E> =
    when (first) {
        is Ok -> Ok((first as Ok<A>).value to second)
        is Err -> first as Err<E>
    }

fun <A, B, E> Pair<A, Result<B, E>>.liftB(): Result<Pair<A, B>, E> =
    when (second) {
        is Ok -> Ok(first to (second as Ok<B>).value)
        is Err -> second as Err<E>
    }

fun <T, E : Throwable> Result<T, E>.orThrow(): T =
    when (this) {
        is Ok -> value
        is Err -> throw error
    }
