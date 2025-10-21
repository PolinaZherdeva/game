package ru.polinazherdeva.task1.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.polinazherdeva.task1.R
import ru.polinazherdeva.task1.data.ScoreRecord

// Адаптер для отображения локальной таблицы рекордов
// Наследуем ListAdapter, чтобы удобно работать с списками и DiffUtil
class LocalLeaderboardAdapter :
    ListAdapter<ScoreRecord, LocalLeaderboardAdapter.ViewHolder>(DiffCallback()) {

    // ViewHolder хранит ссылки на элементы интерфейса для одной строки списка
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlayer: TextView = view.findViewById(R.id.tvPlayerName) // имя игрока
        val tvScore: TextView = view.findViewById(R.id.tvScore)       // очки игрока
        val tvDate: TextView = view.findViewById(R.id.tvDate)         // дата рекорда
    }

    // Создаем ViewHolder, "надувая" XML-разметку для одной строки
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_local_record, parent, false) // привязываем layout к RecyclerView
        return ViewHolder(view)
    }

    // Привязываем данные к ViewHolder (заполняем TextView текущим рекордом)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = getItem(position) // получаем ScoreRecord на текущей позиции
        holder.tvPlayer.text = record.playerName
        holder.tvScore.text = record.score.toString()
        holder.tvDate.text = record.date
    }

    // DiffUtil используется для оптимального обновления RecyclerView (чтобы перерисовывать только измененные строки)
    class DiffCallback : DiffUtil.ItemCallback<ScoreRecord>() {
        // Проверяет, это один и тот же элемент по ID
        override fun areItemsTheSame(oldItem: ScoreRecord, newItem: ScoreRecord) = oldItem.id == newItem.id
        // Проверяет, изменилось ли содержимое элемента
        override fun areContentsTheSame(oldItem: ScoreRecord, newItem: ScoreRecord) = oldItem == newItem
    }
}
