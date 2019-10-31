package com.gligent.exchange.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.objectbox.reactive.DataSubscription
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext


open class BaseViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    // подписки подписчиков
    private val mSubscriptions: MutableList<DataSubscription>

    // обработчик ошибок
    private val handler = CoroutineExceptionHandler { _, throwable ->
        toast.value = throwable.message ?: "Что-то пошло не так :("
    }

    // контект для корутин
    override val coroutineContext: CoroutineContext = Dispatchers.Main + handler + SupervisorJob()

    // liveData для оповещения юзера
    val toast = SingleLiveEvent<String>()

    // добавление в список подписок для очистки в методе  onCleared
    protected fun addSubscription(subscription: DataSubscription) {
        mSubscriptions.add(subscription)
    }

    init {
        mSubscriptions = ArrayList()
    }

    override fun onCleared() {
        super.onCleared()
        for (subscription in mSubscriptions) {
            if (!subscription.isCanceled) {
                subscription.cancel()
            }
        }
    }
}