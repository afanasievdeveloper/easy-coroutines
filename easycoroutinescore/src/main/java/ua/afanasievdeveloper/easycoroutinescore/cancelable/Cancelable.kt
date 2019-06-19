package ua.afanasievdeveloper.easycoroutinescore.cancelable

/**
 * Represents a cancelable resource.
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
interface Cancelable {

    /** Cancel resource. */
    fun cancel()

    companion object {

        /** @return Anonymous class for [Cancelable]. */
        fun create(action: () -> Unit): Cancelable = object : Cancelable {
            override fun cancel() = action()
        }
    }
}

/**
 * Add [Cancelable] to [CompositeCancelable].
 *
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
fun Cancelable.addTo(composite: CompositeCancelable) = composite.add(this)