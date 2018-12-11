package bayrakh.kaktus

import android.support.v7.app.AppCompatActivity
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map

// Класс, который отправляет запрос на сервер каждый раз, когда Activity становится активно
// Принцип работы читай в комментариях к LifecycleTrigger
class HttpTrigger(val factory: () -> Request) : LifecycleTrigger() {
    // Здесь храним объект-запрос
    var request: Request? = null

    // Функция, которая принудительно посылает новый запрос к серверу
    fun forceRecall() {
        off()
        on()
    }

    // При активации посылаем новый запрос, если он ещё не послан
    override fun on() {
        if (request == null) {
            request = factory()
        }
    }

    // При деактивации отменяем текущий запрос, если таковой есть, и очищаем ссылку на него
    override fun off() {
        request?.cancel()
        request = null
    }
}

// Функция помощник, которая добавляет к Activity новый HttpTrigger, привязывая его к событиям жизненного цикла этой Activity
inline fun <reified T: Any> AppCompatActivity.addHttpTrigger(
        crossinline factory: () -> Request,
        noinline handler: (Request, Response, Result<T, Exception>) -> Unit
): HttpTrigger {
    val result = HttpTrigger {
        factory().responseString(Charsets.UTF_8) { req, resp, result ->
            val newResult = result.map {
                Klaxon().parse<T>(it) ?: throw KlaxonException("Cannot parse ${T::class.qualifiedName}")
            }
            runOnUiThread {
                handler(req, resp, newResult)
            }
        }
    }
    lifecycle.addObserver(result)
    return result
}
