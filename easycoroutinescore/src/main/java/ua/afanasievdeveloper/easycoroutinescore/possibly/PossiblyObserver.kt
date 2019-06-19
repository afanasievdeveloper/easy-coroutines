package ua.afanasievdeveloper.easycoroutinescore.possibly

/**
 * Observer for [Possibly] result.
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
interface PossiblyObserver<T> {

    /** Handle null. */
    fun onComplete(): Unit?

    /** Handle result. */
    fun onSuccess(value: T): Unit?

    /** Handle unsuccessful result. */
    fun onFailure(error: Throwable): Unit?
}

/**
 * @return Anonymous class for [PossiblyObserver].
 *
 * @param complete Callback for coroutine execution with null.
 * @param success Callback for coroutine execution with result.
 * @param failure Callback for unsuccessful execution.
 *
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
@Suppress("FunctionName")
fun <T> PossiblyObserver(
    complete: (() -> Unit)?,
    success: ((T) -> Unit)?,
    failure: ((Throwable) -> Unit)?
) = object : PossiblyObserver<T> {

    override fun onComplete() = complete?.invoke()

    override fun onSuccess(value: T) = success?.invoke(value)

    override fun onFailure(error: Throwable) = failure?.invoke(error)
}