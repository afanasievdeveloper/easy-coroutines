package ua.afanasievdeveloper.easycoroutinescore.possibly

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.afanasievdeveloper.easycoroutinescore.cancelable.Cancelable
import kotlin.coroutines.CoroutineContext

class Possibly<T>(
    private val subscribeContext: CoroutineContext,
    private val observeContext: CoroutineContext
) : CoroutineScope {

    private var supplier: (suspend () -> T?)? = null
    private var onSubscribeAction: (() -> Unit)? = null

    override val coroutineContext: CoroutineContext
        get() = subscribeContext

    fun from(supplier: suspend () -> T?): Possibly<T> {
        this.supplier = supplier
        return this
    }

    fun doOnSubscribe(action: () -> Unit): Possibly<T> {
        onSubscribeAction = action
        return this
    }

    fun subscribe(
        onComplete: () -> Unit,
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit
    ): Cancelable {
        val observer = PossiblyObserver(onComplete, onSuccess, onFailure)
        return subscribe(observer)
    }

    fun subscribe(observer: PossiblyObserver<T>): Cancelable {
        requireNotNull(supplier)
        val job = createJob(observer)
        return Cancelable.create { job.cancel() }
    }

    private fun createJob(observer: PossiblyObserver<T>): Job = launch {
        withContext(observeContext) { onSubscribeAction?.invoke() }
        try {
            val result = requireNotNull(supplier)()
            withContext(observeContext) {
                result?.also { observer.onSuccess(it) } ?: observer.onComplete()
            }
        } catch (error: Throwable) {
            withContext(observeContext) { observer.onFailure(error) }
        }
    }
}