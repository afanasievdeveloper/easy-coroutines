package ua.afanasievdeveloper.easycoroutinescore.simply

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.afanasievdeveloper.easycoroutinescore.cancelable.Cancelable
import ua.afanasievdeveloper.easycoroutinescore.lonely.LonelyObserver
import kotlin.coroutines.CoroutineContext

/**
 * Wrapper for a coroutine that returns no value.
 * Implements [CoroutineScope].
 *
 * @param subscribeContext Context for coroutine execution.
 * @param observeContext Context for callbacks execution.
 *
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
class Simply(
    private val subscribeContext: CoroutineContext,
    private val observeContext: CoroutineContext
) : CoroutineScope {

    private var action: (suspend () -> Unit)? = null
    private var onSubscribeAction: (() -> Unit)? = null

    override val coroutineContext: CoroutineContext
        get() = subscribeContext

    /**
     * Initialization of action.
     * @param action Lambda which will be executed.
     */
    fun from(action: suspend () -> Unit): Simply {
        this.action = action
        return this
    }

    /**
     * Initialization of on subscribe action.
     * @param action Lambda which will be executed after subscription.
     */
    fun doOnSubscribe(action: () -> Unit): Simply {
        onSubscribeAction = action
        return this
    }

    /**
     * Subscribes for a coroutine.
     * Creates [SimplyObserver] and calls another subscribe method for it.
     *
     * @param onComplete Callback for successful coroutine execution.
     * @param onFailure Callback for unsuccessful coroutine execution.
     *
     * @return [Cancelable].
     */
    fun subscribe(
        onComplete: (() -> Unit)? = null,
        onFailure: ((Throwable) -> Unit)? = null
    ): Cancelable {
        val observer = SimplyObserver(onComplete, onFailure)
        return subscribe(observer)
    }

    /**
     * Subscribes for a coroutine.
     * Calls [createJob] method.
     *
     * @param observer [SimplyObserver] for a coroutine.
     *
     * @return [Cancelable].
     */
    fun subscribe(observer: SimplyObserver): Cancelable {
        requireNotNull(action)
        val job = createJob(observer)
        return Cancelable.create { job.cancel() }
    }

    /**
     * Creates a [Job] with [launch].
     * Executes [onSubscribeAction] at first in [observeContext].
     * Executes [action] in [coroutineContext].
     * Executes [observer] callbacks in [observeContext].
     *
     * @param observer [LonelyObserver] for a coroutine.
     */
    private fun createJob(observer: SimplyObserver): Job = launch {
        withContext(observeContext) { onSubscribeAction?.invoke() }
        try {
            requireNotNull(action)()
            withContext(observeContext) { observer.onComplete() }
        } catch (error: Throwable) {
            withContext(observeContext) { observer.onFailure(error) }
        }
    }
}