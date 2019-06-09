package ua.afanasievdeveloper.easycoroutinescore.lonely

interface LonelyObserver<T> {

    fun onSuccess(value: T): Unit?

    fun onFailure(error: Throwable): Unit?
}

@Suppress("FunctionName")
fun <T> LonelyObserver(success: ((T) -> Unit)?, failure: ((Throwable) -> Unit)?) = object : LonelyObserver<T> {

    override fun onSuccess(value: T) = success?.invoke(value)

    override fun onFailure(error: Throwable) = failure?.invoke(error)
}