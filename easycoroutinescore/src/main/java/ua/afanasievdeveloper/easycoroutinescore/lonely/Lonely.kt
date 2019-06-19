package ua.afanasievdeveloper.easycoroutinescore.lonely

import kotlinx.coroutines.*
import ua.afanasievdeveloper.easycoroutinescore.cancelable.Cancelable
import kotlin.coroutines.CoroutineContext

/**
 * Wrapper for a coroutine that returns a value.
 * Implements [CoroutineScope].
 *
 * @param subscribeContext Context for coroutine execution.
 * @param observeContext Context for callbacks execution.
 *
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
class Lonely<T>(
    private val subscribeContext: CoroutineContext,
    private val observeContext: CoroutineContext
) : CoroutineScope {

    private var supplier: (suspend () -> T)? = null
    private var onSubscribeAction: (() -> Unit)? = null

    override val coroutineContext: CoroutineContext
        get() = subscribeContext

    /**
     * Initialization of data supplier.
     * @param supplier Lambda which will be executed to get the result.
     */
    fun from(supplier: suspend () -> T): Lonely<T> {
        this.supplier = supplier
        return this
    }

    /**
     * Initialization of on subscribe action.
     * @param action Lambda which will be executed after subscription.
     */
    fun doOnSubscribe(action: () -> Unit): Lonely<T> {
        onSubscribeAction = action
        return this
    }

    /**
     * Subscribes for a coroutine.
     * Creates [LonelyObserver] and calls another subscribe method for it.
     *
     * @param onSuccess Callback for successful coroutine execution.
     * @param onFailure Callback for unsuccessful coroutine execution.
     *
     * @return [Cancelable].
     */
    fun subscribe(
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((Throwable) -> Unit)? = null
    ): Cancelable {
        val observer = LonelyObserver(onSuccess, onFailure)
        return subscribe(observer)
    }

    /**
     * Subscribes for a coroutine.
     * Calls [createJob] method.
     *
     * @param observer [LonelyObserver] for a coroutine.
     *
     * @return [Cancelable].
     */
    fun subscribe(observer: LonelyObserver<T>): Cancelable {
        requireNotNull(supplier)
        val job = createJob(observer)
        return Cancelable.create { job.cancel() }
    }

    /**
     * Creates a [Job] with [launch].
     * Executes [onSubscribeAction] at first in [observeContext].
     * Executes [supplier] in [coroutineContext].
     * Executes [observer] callbacks in [observeContext].
     *
     * @param observer [LonelyObserver] for a coroutine.
     */
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

/**
 * Combining of two suppliers in one [Lonely].
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
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