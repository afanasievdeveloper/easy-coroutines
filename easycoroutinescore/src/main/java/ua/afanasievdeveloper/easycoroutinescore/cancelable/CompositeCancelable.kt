package ua.afanasievdeveloper.easycoroutinescore.cancelable

/**
 * Container for [Cancelable] collection.
 * @author A. Afanasiev (https://github.com/afanasievdeveloper).
 */
class CompositeCancelable : Cancelable {

    private val cancelables = mutableListOf<Cancelable>()

    override fun cancel() = with(cancelables) {
        forEach { it.cancel() }
        clear()
    }

    /** Add [Cancelable] to collection. */
    fun add(cancelable: Cancelable) = cancelables.add(cancelable)
}