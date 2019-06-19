package ua.afanasievdeveloper.easycoroutinescore.lonely

/**
 * Observer for [Lonely] result.
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
interface LonelyObserver<T> {

    /** Handle successful result. */
    fun onSuccess(value: T): Unit?

    /** Handle unsuccessful result. */
    fun onFailure(error: Throwable): Unit?
}

/**
 * @return Anonymous class for [LonelyObserver].
 *
 * @param success Callback for successful result.
 * @param failure Callback for unsuccessful result.
 *
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
@Suppress("FunctionName")
fun <T> LonelyObserver(success: ((T) -> Unit)?, failure: ((Throwable) -> Unit)?) = object : LonelyObserver<T> {

    override fun onSuccess(value: T) = success?.invoke(value)

    override fun onFailure(error: Throwable) = failure?.invoke(error)
}