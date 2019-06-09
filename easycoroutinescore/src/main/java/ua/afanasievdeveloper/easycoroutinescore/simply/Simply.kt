package ua.afanasievdeveloper.easycoroutinescore.simply

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.afanasievdeveloper.easycoroutinescore.cancelable.Cancelable
import kotlin.coroutines.CoroutineContext

class Simply(
    private val subscribeContext: CoroutineContext,
    private val observeContext: CoroutineContext
) : CoroutineScope {

    private var action: (suspend () -> Unit)? = null
    private var onSubscribeAction: (() -> Unit)? = null

    override val coroutineContext: CoroutineContext
        get() = subscribeContext

    fun from(action: suspend () -> Unit): Simply {
        this.action = action
        return this
    }

    fun doOnSubscribe(action: () -> Unit): Simply {
        onSubscribeAction = action
        return this
    }

    fun subscribe(
        onComplete: (() -> Unit)?,
        onFailure: ((Throwable) -> Unit)?
    ): Cancelable {
        val observer = SimplyObserver(onComplete, onFailure)
        return subscribe(observer)
    }

    fun subscribe(observer: SimplyObserver): Cancelable {
        requireNotNull(action)
        val job = createJob(observer)
        return Cancelable.create { job.cancel() }
    }

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