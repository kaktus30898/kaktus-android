package bayrakh.kaktus

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent

// Класс-помощник для привязки к жизненному циклу Activity.
// При создании, показе или фокусировке Activity вызывает метод on.
// При расфокусировке, сокрытии или уничтожении вызывает метод off.
abstract class LifecycleTrigger : LifecycleObserver {

    private var state = false

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onLifecycleEvent(owner: LifecycleOwner, event: Lifecycle.Event) {
        val requestedState = when (event) {
            Lifecycle.Event.ON_CREATE -> true
            Lifecycle.Event.ON_START -> true
            Lifecycle.Event.ON_RESUME -> true
            Lifecycle.Event.ON_PAUSE -> false
            Lifecycle.Event.ON_STOP -> false
            Lifecycle.Event.ON_DESTROY -> false
            Lifecycle.Event.ON_ANY -> state
        }

        if (requestedState != state) {
            if (requestedState) {
                on()
            } else {
                off()
            }
            state = requestedState
        }
    }

    abstract fun on()
    abstract fun off()
}
