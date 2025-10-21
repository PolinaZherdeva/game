package ru.polinazherdeva.task1.data

// Импорты Room для работы с таблицами
import androidx.room.Entity // @Entity — аннотация, которая делает класс таблицей в базе данных Room
import androidx.room.PrimaryKey // @PrimaryKey — аннотация для поля, которое будет первичным ключом таблицы


@Entity(tableName = "score_records")
data class ScoreRecord(
    // autoGenerate = true — ключ создаётся автоматически SQLite при вставке записи.
    //val id: Long = 0 — значение по умолчанию 0, Room заменит его на уникальное.
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playerName: String = "", // поле имени игрока
    val score: Int, // поле очков игрока
    val date: String = "" // дата записи результата
)