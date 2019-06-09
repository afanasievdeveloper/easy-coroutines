package ua.afanasievdeveloper.easycoroutinescore.possibly

interface PossiblyObserver<T> {

    fun onComplete()

    fun onSuccess(value: T)

    fun onFailure(error: Throwable)
}

@Suppress("FunctionName")
fun <T> PossiblyObserver(
    complete: () -> Unit,
    success: (T) -> Unit,
    failure: (Throwable) -> Unit
) = object : PossiblyObserver<T> {

    override fun onComplete() = complete()

    override fun onSuccess(value: T) = success(value)

    override fun onFailure(error: Throwable) = failure(error)
}