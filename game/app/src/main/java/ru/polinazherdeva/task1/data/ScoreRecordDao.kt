package ru.polinazherdeva.task1.data

import androidx.room.Dao // @Dao — аннотация для интерфейса, через который мы работаем с таблицей
import androidx.room.Insert // Insert — для вставки данных
import androidx.room.OnConflictStrategy
import androidx.room.Query // для выполнения SQL-запросов
import kotlinx.coroutines.flow.Flow

@Dao // Объявляем DAO (Data Access Object)
interface ScoreRecordDao {

    // Возвращает Long — id вставленной записи
    // onConflict = REPLACE — если уже есть запись с таким id, заменяем её
    // suspend — функция для корутин, асинхронная операци
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(record: ScoreRecord): Long

    // @Query — выполняет SQL-запрос: выбираем все записи
    // Возвращает Flow<List<ScoreRecord>> — поток списка записей, обновляется автоматически при изменении таблицы
    @Query("SELECT * FROM score_records ORDER BY score DESC")
    fun getAllSortedDesc(): Flow<List<ScoreRecord>>

    // SQL-запрос удаляет все записи из таблицы
    @Query("DELETE FROM score_records")
    suspend fun clearAll()
}