package ua.afanasievdeveloper.easycoroutinescore.lonely

import kotlinx.coroutines.*
import ua.afanasievdeveloper.easycoroutinescore.cancelable.Cancelable
import kotlin.coroutines.CoroutineContext

class Lonely<T>(
    private val subscribeContext: CoroutineContext,
    private val observeContext: CoroutineContext
) : CoroutineScope {

    private var supplier: (suspend () -> T)? = null
    private var onSubscribeAction: (() -> Unit)? = null

    override val coroutineContext: CoroutineContext
        get() = subscribeContext

    fun from(supplier: suspend () -> T): Lonely<T> {
        this.supplier = supplier
        return this
    }

    fun doOnSubscribe(action: () -> Unit): Lonely<T> {
        onSubscribeAction = action
        return this
    }

    fun subscribe(
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((Throwable) -> Unit)? = null
    ): Cancelable {
        val observer = LonelyObserver(onSuccess, onFailure)
        return subscribe(observer)
    }

    fun subscribe(observer: LonelyObserver<T>): Cancelable {
        requireNotNull(supplier)
        val job = createJob(observer)
        return Cancelable.create { job.cancel() }
    }

    private fun createJob(observer: LonelyObserver<T>): Job = launch {
        withContext(observeContext) { onSubscribeAction?.invoke() }
        try {
            val result = requireNotNull(supplier)()
            withContext(observeContext) { observer.onSuccess(result) }
        } catch (error: Throwable) {
            withContext(observeContext) { observer.onFailure(error) }
        }
    }
}

fun <T1, T2, R> Lonely<R>.combine(
    supplier1: suspend () -> T1,
    supplier2: suspend () -> T2,
    consumer: (T1, T2) -> R
): Lonely<R> {
    val supplier: suspend () -> R = {
        val result1 = CoroutineScope(coroutineContext).async { supplier1() }
        val result2 = CoroutineScope(coroutineContext).async { supplier2() }
        consumer(result1.await(), result2.await())
    }
    return from(supplier)
}