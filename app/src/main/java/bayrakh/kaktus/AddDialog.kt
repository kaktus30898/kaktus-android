package bayrakh.kaktus

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

// Класс, описывающий поведение диалога добавления траты
class AddDialog : DialogFragment() {
    // Сюда записывается поле ввода описания
    private var description: EditText? = null
    // Сюда записывается поле ввода значения
    private var value: EditText? = null
    // Сюда записывается кнопка "Создать"
    private var create: Button? = null
    // Сюда записывается функция, которая вызывается после "создания"
    var callback: ((BudgetChange) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Создаём отображение
        return inflater.inflate(R.layout.add_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Записываем поля
        description = view.findViewById(R.id.description)
        value = view.findViewById(R.id.value)
        create = view.findViewById(R.id.create)
        // Добавляем кнопке обработчик по клику
        create?.setOnClickListener {
            // Если description не null, то получаем его текст, иначе ничего не делаем
            description?.text.toString().let { description ->
                // Если value не null, то получаем его текст и пытаемся преобразовать в число.
                // В случае ошибки, ничего не делаем.
                value?.text.toString().toDoubleOrNull()?.let { value ->
                    // Если всё получено успешно, формируем объект BudgetChange и передаём его в обработчик.
                    callback?.invoke(BudgetChange(
                            id = -1,
                            caption = description,
                            delta = value
                    ))
                    // А затем закрываем диалог
                    dismiss()
                }
            }
        }
    }
}

// Функция-помощник, которая инициализирует диалог, устанавливает обработчик и показывает его
fun AppCompatActivity.showAddDialog(callback: (BudgetChange) -> Unit): AddDialog {
    val dialog = AddDialog()
    dialog.callback = callback
    dialog.show(supportFragmentManager, "add-dialog")
    return dialog
}
