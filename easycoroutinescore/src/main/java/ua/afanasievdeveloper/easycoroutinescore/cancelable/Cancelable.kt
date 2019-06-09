package ua.afanasievdeveloper.easycoroutinescore.cancelable

interface Cancelable {

    fun cancel()

    companion object {
        fun create(action: () -> Unit): Cancelable = object : Cancelable {
            override fun cancel() = action()
        }
    }
}

fun Cancelable.addTo(composite: CompositeCancelable) = composite.add(this)