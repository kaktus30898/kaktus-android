package bayrakh.kaktus

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Этот класс поможет android привязать данные класса BudgetChange к отображению.
 */
class BudgetChangeAdapter(private val context: Context, list: List<BudgetChange> = listOf())
    : RecyclerView.Adapter<BudgetChangeAdapter.BudgetChangeViewHolder>() {

    private var _list: List<BudgetChange> = listOf()

    var list: List<BudgetChange>
        get() = _list
        set(newValue) {
            _list = newValue.sortedBy { it.id }.reversed()
            notifyDataSetChanged()
        }

    init {
        this.list = list
    }

    // Этот метод возвращает количество элементов в списке
    override fun getItemCount() = _list.size

    // Этот - вызывает привязку данных
    override fun onBindViewHolder(holder: BudgetChangeAdapter.BudgetChangeViewHolder, position: Int) {
        holder.fromValue(_list[position])
    }

    // Этот - создаёт новый объект отображения. Его код был скопировать со StackOverflow и не будет изменяться в будущем.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetChangeAdapter.BudgetChangeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.budget_change, parent, false)
        return BudgetChangeAdapter.BudgetChangeViewHolder(context, view)
    }

    // Этот класс отвечает за привязку данных элемента к элементу отображения.
    class BudgetChangeViewHolder(private val context: Context, itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        // Тут мы дёргаем из отображения ссылки на элементы.
        // Если они не будут найдены, то станут null и привязка сломается (но не уронит приложение).
        val view = itemView
        val caption = itemView.findViewById(R.id.caption) as TextView
        val delta = itemView.findViewById(R.id.delta) as TextView

        // Этот метод привязывает данные из полученного объекта элементам, ссылки на которые были получены при инициализации.
        fun fromValue(obj: BudgetChange) {
            caption.text = obj.caption
            delta.text = context.getString(R.string.money_value, obj.delta)
            view.setOnClickListener {
                view.context.showChangeInfo(obj)
            }
        }
    }
}