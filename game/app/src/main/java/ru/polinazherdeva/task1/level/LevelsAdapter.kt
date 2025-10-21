package ru.polinazherdeva.task1.level

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.polinazherdeva.task1.R

// создаем адаптер, который совмещает данные (levels) к элементам списка (RecyclerView)
class LevelsAdapter(
    private val levels: List<Level>, // список объектов Level для отображения
    // лямбда-функция, вызывается при нажатии на элемент списка
    private val onItemClick: (Level) -> Unit
) : RecyclerView.Adapter<LevelsAdapter.LevelViewHolder>() {

    // LevelViewHolder хранит ссылки на название уровня и его сложность (контейнер для отображения)
    // itemView — корневой View для одной карточки (элемента списка)
    // ViewHolder — хранит ссылки на элементы интерфейса внутри одной ячейки списка
    inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvLevelName)
        val difficulty: TextView = itemView.findViewById(R.id.tvLevelDifficulty)
    }

    // Создаёт новую карточку уровня, когда она нужна
    // LayoutInflater.from(...) — превращает XML (item_level.xml) в объект View.
    //inflate(...) — “надувает” XML-файл (создаёт объект на экране).
    //Возвращает новый LevelViewHolder, который будет хранить ссылки на элементы этого View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_level, parent, false)
        return LevelViewHolder(view)
    }

    // Вызывается каждый раз, когда нужно отобразить элемент списка
    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        val level = levels[position] // берём объект Level по позиции
        holder.name.text = level.name // вставляем название уровня
        holder.difficulty.text = level.difficulty // вставляем сложность
        holder.itemView.setOnClickListener { onItemClick(level) } // если пользователь нажал на карточку, вызываем переданную функцию onItemClick(level)
    }

    override fun getItemCount() = levels.size // возвращает кол-во элементов в списке, надо для RecyclerView
}