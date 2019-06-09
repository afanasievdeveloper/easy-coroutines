package ua.afanasievdeveloper.easycoroutinescore.cancelable

class CompositeCancelable : Cancelable {

    private val cancelables = mutableListOf<Cancelable>()

    override fun cancel() = with(cancelables) {
        forEach { it.cancel() }
        clear()
    }

    fun add(cancelable: Cancelable) = cancelables.add(cancelable)
}