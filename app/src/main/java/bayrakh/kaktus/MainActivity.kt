package bayrakh.kaktus

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.github.kittinunf.result.failure
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable

// Класс, описывающий поведение главного Activity приложения
class MainActivity : AppCompatActivity(), Serializable {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Инциализация отображения
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация списка
        changes.layoutManager = LinearLayoutManager(this)
        val changesAdapter = BudgetChangeAdapter(this)
        changes.adapter = changesAdapter

        // Добавляем привязанный к жизненному циклу запрос к серверу данных.
        addBudgetChangeTrigger { _, _, result ->
            // При получении, проверяем успешность чтения полученных данных
            result.fold({ it: BudgetChangeList ->
                // Если всё хорошо, обновляем данные в отображении
                rest?.text = getString(R.string.money_value, it.rest)
                changesAdapter.list = it.list.toList()
            }, {
                // Иначе логгируем ошибку
                it.printStackTrace()
            })
        }

        // Добавляем обработчик по клику на кнопку "плюс"
        addAction.setOnClickListener {
            // Показывать диалог создания траты
            showChangeInfo()
        }
    }
}
