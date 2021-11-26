package eu.flowcode.play

import android.util.Log
import androidx.compose.runtime.sourceInformationMarkerEnd
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {
    //State
    private var _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()

    //Event
    private var _counterShared = MutableSharedFlow<Int>()
    val counterShared = _counterShared.asSharedFlow()

    private var _counterChannel = Channel<Int>()

    private val compositeDisposable = CompositeDisposable()


    val flow = flow<Int> {
        (1..10).map {
            delay(1000)
            emit(it)
        }
    }

    fun incrementCounter() {
        _counter.value++
        viewModelScope.launch {
            _counterShared.emit(_counter.value)
        }
    }

    fun onClickManageFlow() {

    }

    fun onClickRx() {
        compositeDisposable.add(
            Observable.create<String> { source ->
                if (!source.isDisposed) {
                    source.onNext("Test")
                    source.onComplete()
                }
            }
                .subscribe { log(it) })

        compositeDisposable.add(
            Observable.just(1, 2)
                .subscribeOn(Schedulers.io())
                .doOnNext { log("On next: $it - ${Thread.currentThread().name}") }
                .observeOn(Schedulers.newThread())
                .doOnNext { log("On next 2: $it - ${Thread.currentThread().name}") }
                .flatMap { value ->
                    Observable.just(10, 10, 10)
                        .delay(1, TimeUnit.SECONDS)
                        .map { it + value }
                        .reduce { t1, t2 -> t1 + t2 }.toObservable()
                        .subscribeOn(Schedulers.computation())
                }
                .doOnNext { log("On next 3: $it - ${Thread.currentThread().name}") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { log("On subscribe: $it - ${Thread.currentThread().name}") }
        )
    }

    private fun log(s: String) = Log.e("Log", s)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}