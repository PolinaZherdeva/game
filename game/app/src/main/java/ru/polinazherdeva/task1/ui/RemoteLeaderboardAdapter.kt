package ru.polinazherdeva.task1.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.polinazherdeva.task1.R
import ru.polinazherdeva.task1.network.RemoteRecord
import ru.polinazherdeva.task1.util.formatServerDateToDisplay

// Адаптер для отображения топ-5 рекордов с сервера
// Наследуется от ListAdapter для удобной работы с DiffUtil
class RemoteLeaderboardAdapter :
    ListAdapter<RemoteRecord, RemoteLeaderboardAdapter.ViewHolder>(DiffCallback()) {

    // ViewHolder хранит ссылки на элементы интерфейса для одной строки списка
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlayer: TextView = view.findViewById(R.id.tvPlayerNameRemote) // имя игрока
        val tvScore: TextView = view.findViewById(R.id.tvScoreRemote)       // очки игрока
        val tvDate: TextView = view.findViewById(R.id.tvDateRemote)         // дата рекорда
    }

    // Создаем ViewHolder, "надувая" XML-разметку для одной строки
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_remote_record, parent, false) // привязываем layout к RecyclerView
        return ViewHolder(view)
    }

    // Привязываем данные к ViewHolder (заполняем TextView текущим рекордом)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = getItem(position) // получаем RemoteRecord на текущей позиции
        holder.tvPlayer.text = record.playerName
        holder.tvScore.text = record.score.toString()
        // Преобразуем дату из формата сервера YYYY-MM-DD в DD.MM.YYYY
        holder.tvDate.text = record.date.formatServerDateToDisplay()
    }

    // DiffUtil позволяет RecyclerView обновлять только изменившиеся строки, не перерисовывая весь список
    class DiffCallback : DiffUtil.ItemCallback<RemoteRecord>() {
        // Проверяем, это один и тот же элемент (здесь по имени игрока)
        override fun areItemsTheSame(oldItem: RemoteRecord, newItem: RemoteRecord) = oldItem.playerName == newItem.playerName
        // Проверяем, изменилось ли содержимое элемента
        override fun areContentsTheSame(oldItem: RemoteRecord, newItem: RemoteRecord) = oldItem == newItem
    }
}