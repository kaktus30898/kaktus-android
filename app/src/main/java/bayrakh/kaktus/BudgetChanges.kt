package bayrakh.kaktus

import android.support.v7.app.AppCompatActivity
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map
import org.json.JSONObject
import java.io.Serializable

// Объект-помощник для связи с сервером данных
object DataServer {
    private const val url = "http://10.0.2.2:5000/graphql"

    // Функция, инициализирующая запрос к серверу данных
    fun request(graphql: GraphQLRequest): Request {
        val req = url.httpPost()
                .body(graphql.toJSON().toString())
        req.headers["Content-Type"] = "application/json"
        return req
    }
}

// Класс, описывающий изменение бюджета
data class BudgetChange(val id: Int?, val caption: String, val delta: Double) : Serializable {
    // Функция, которая преобразует объект в JSON
    fun toJSON(withID: Boolean = true) = if (withID) {
        JSONObject(mapOf(
                "id" to id,
                "caption" to caption,
                "delta" to delta
        ))
    } else {
        JSONObject(mapOf(
                "caption" to caption,
                "delta" to delta
        ))
    }

    // Функция, которая отправляет серверу данных запрос на запись нового изменения
    fun save(): Request {
        return DataServer.request(if (id != null) {
            GraphQLRequest(
                    "mutation UpdateIt(\$id: Int!, \$change: BudgetChangeBody!) { saveChange(id: \$id, change: \$change) }",
                    "id" to id,
                    "change" to toJSON(false)
            )
        } else {
            GraphQLRequest(
                    "mutation AddIt(\$change: BudgetChangeBody!) { addChange(change: \$change) }",
                    "change" to toJSON(false)
            )
        })
    }

    fun delete(): Request? {
        id?.let { id ->
            return DataServer.request(GraphQLRequest(
                    "mutation DeleteIt(\$id: Int!) { deleteChange(id: \$id)}",
                    "id" to id
            ))
        }
    }
}

// Класс-помощник для хранения информации, полученной от сервера данных
data class BudgetChangeList(val rest: Double, val list: Collection<BudgetChange>)

// Класс-помощник для хранения информации, полученной от сервера данных
data class BudgetChangeQuery(val changes: BudgetChangeList)

// Класс-помощник для хранения информации, полученной от сервера данных
data class BudgetChangeQueryGraphQLResponse(val data: BudgetChangeQuery)

// Функция-помощник, которая добавляет к Activity объект HttpTrigger (его действие смотри в комментариях к нему).
// В обработчик будет передан BudgetChangeList, который содержит список изменений и остаток на балансе.
fun AppCompatActivity.addBudgetChangeTrigger(
        handler: (Request, Response, Result<BudgetChangeList, Exception>) -> Unit
): HttpTrigger {
    return addHttpTrigger<BudgetChangeQueryGraphQLResponse>({
        // Инициализируем запрос данных с сервера
        DataServer.request(GraphQLRequest(
                query = "query GetList { changes { list { id, caption, delta } rest } }"
        ))
    }) { req, resp, result ->
        // Преобразовываем полученные данные и направляем их в обработчик
        handler(req, resp, result.map { it.data.changes })
    }
}

// Класс-помощник для хранения данных о запросе к серверу данных
data class GraphQLRequest(val query: String, val variables: Map<String, Any> = mapOf()) {
    // Конструктор, упрощающий инициализацию
    constructor(query: String, vararg variables: Pair<String, Any>) : this(query, variables.toMap())

    // Функция, которая преобразует объект в JSON
    fun toJSON() = JSONObject(mapOf(
            "query" to query,
            "variables" to variables
    ))
}
