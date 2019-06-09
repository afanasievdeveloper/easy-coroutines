package ua.afanasievdeveloper.easycoroutinescore.simply

interface SimplyObserver {

    fun onComplete(): Unit?

    fun onFailure(error: Throwable): Unit?
}

@Suppress("FunctionName")
fun SimplyObserver(complete: (() -> Unit)?, failure: ((Throwable) -> Unit)?) = object : SimplyObserver {
    override fun onComplete() = complete?.invoke()
    override fun onFailure(error: Throwable) = failure?.invoke(error)
}