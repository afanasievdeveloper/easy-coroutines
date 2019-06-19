package ua.afanasievdeveloper.easycoroutinescore.possibly

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.afanasievdeveloper.easycoroutinescore.cancelable.Cancelable
import kotlin.coroutines.CoroutineContext

/**
 * Wrapper for a coroutine that returns a nullable value.
 * Implements [CoroutineScope].
 *
 * @param subscribeContext Context for coroutine execution.
 * @param observeContext Context for callbacks execution.
 *
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
class Possibly<T>(
    private val subscribeContext: CoroutineContext,
    private val observeContext: CoroutineContext
) : CoroutineScope {

    private var supplier: (suspend () -> T?)? = null
    private var onSubscribeAction: (() -> Unit)? = null

    override val coroutineContext: CoroutineContext
        get() = subscribeContext

    /**
     * Initialization of data supplier.
     * @param supplier Lambda which will be executed to get the result.
     */
    fun from(supplier: suspend () -> T?): Possibly<T> {
        this.supplier = supplier
        return this
    }

    /**
     * Initialization of on subscribe action.
     * @param action Lambda which will be executed after subscription.
     */
    fun doOnSubscribe(action: () -> Unit): Possibly<T> {
        onSubscribeAction = action
        return this
    }

    /**
     * Subscribes for a coroutine.
     * Creates [PossiblyObserver] and calls another subscribe method for it.
     *
     * @param onComplete Callback for coroutine execution with null.
     * @param onSuccess Callback for coroutine execution with result.
     * @param onFailure Callback for unsuccessful coroutine execution.
     *
     * @return [Cancelable].
     */
    fun subscribe(
        onComplete: (() -> Unit)? = null,
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((Throwable) -> Unit)? = null
    ): Cancelable {
        val observer = PossiblyObserver(onComplete, onSuccess, onFailure)
        return subscribe(observer)
    }

    /**
     * Subscribes for a coroutine.
     * Calls [createJob] method.
     *
     * @param observer [PossiblyObserver] for a coroutine.
     *
     * @return [Cancelable].
     */
    fun subscribe(observer: PossiblyObserver<T>): Cancelable {
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
     * @param observer [PossiblyObserver] for a coroutine.
     */
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