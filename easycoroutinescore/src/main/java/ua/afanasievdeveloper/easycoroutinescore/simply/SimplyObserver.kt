package ua.afanasievdeveloper.easycoroutinescore.simply

/**
 * Observer for [Simply] execution.
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
interface SimplyObserver {

    /** Handle successful result. */
    fun onComplete(): Unit?

    /** Handle unsuccessful result. */
    fun onFailure(error: Throwable): Unit?
}

/**
 * @return Anonymous class for [SimplyObserver].
 *
 * @param complete Callback for successful result.
 * @param failure Callback for unsuccessful result.
 *
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
@Suppress("FunctionName")
fun SimplyObserver(complete: (() -> Unit)?, failure: ((Throwable) -> Unit)?) = object : SimplyObserver {
    override fun onComplete() = complete?.invoke()
    override fun onFailure(error: Throwable) = failure?.invoke(error)
}