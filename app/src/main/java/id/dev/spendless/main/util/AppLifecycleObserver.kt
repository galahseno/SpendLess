package id.dev.spendless.main.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppLifecycleObserver(
    private val coroutineScope: CoroutineScope,
    private val onAppBackground: () -> Unit,
    private val onAppCreate: () -> Unit,
) : DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        coroutineScope.launch {
            onAppBackground()
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        coroutineScope.launch {
            onAppCreate()
        }
    }
}