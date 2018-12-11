package bayrakh.kaktus

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.github.kittinunf.result.failure
import kotlinx.android.synthetic.main.activity_change_info.*

class ChangeInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_info)

        val change = intent.extras?.getSerializable("change") as BudgetChange?

        if (change === null) {
            /**
             * Если не передан объект изменения бюджета, то показываем информацию о том,
             * что мы создаём новую запись для базы. Кнопку удаления скрываем.
             */
            title_text.text = getString(R.string.new_change_title)
            description.setText("")
            value.setText("")
            saveButton.text = getString(R.string.create)
            deleteButton.visibility = INVISIBLE
            deleteButton.setOnClickListener(null)
        } else {
            /**
             * Если необъект изменения бюджета был передан, то показываем информацию о том,
             * что мы редактируем существующую запись для базы. Кнопку удаления показываем.
             */
            title_text.text = getString(R.string.change_title)
            description.setText(change.caption)
            value.setText(change.delta.toString())
            saveButton.text = getString(R.string.save)
            deleteButton.visibility = VISIBLE
            deleteButton.setOnClickListener {
                // Отправляем запрос на удаление
                val req = change.delete()
                if (req == null) {
                    // Если запрос не был отправлен, просто закрываем activity
                    finish()
                } else {
                    // Если был, устанавливаем обработчик завершения запроса
                    req.responseString { _, _, result ->
                        // При получении ответа, проверяем результат на ошибку
                        result.failure { err ->
                            // В случае ошибки, логгируем её
                            err.printStackTrace()
                        }
                        // А затем закрываем activity
                        finish()
                    }
                }
            }
        }

        saveButton.setOnClickListener {
            value.text.toString().toDoubleOrNull()?.let { value ->
                BudgetChange(
                        change?.id,
                        description.text.toString(),
                        value
                ).save().responseString { _, _, result ->
                    // При получени, проверяем результат на ошибку
                    result.failure { err ->
                        // В случае ошибки, логгируем её
                        err.printStackTrace()
                    }
                    // А затем закрываем activity
                    finish()
                }
            }
        }
    }
}

fun Context.showChangeInfo(change: BudgetChange? = null) {
    startActivity(Intent(this, ChangeInfo::class.java).apply {
        putExtra("change", change)
    })
}
