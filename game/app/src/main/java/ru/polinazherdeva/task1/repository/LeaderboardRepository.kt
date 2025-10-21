package ru.polinazherdeva.task1.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import ru.polinazherdeva.task1.data.ScoreRecordDao // if placed under `data` package; adapt import
import ru.polinazherdeva.task1.data.ScoreRecord
import ru.polinazherdeva.task1.network.RemoteApi
import ru.polinazherdeva.task1.network.RemoteRecord
import java.text.SimpleDateFormat
import java.util.*

class LeaderboardRepository(
    private val dao: ScoreRecordDao, // локальный dao
    private val api: RemoteApi // удаленный api
) {
    // Возвращаем Flow списка локальных записей, делегируя DAO
    // Поскольку DAO возвращает Flow, любая подписка получит обновления при изменении БД
    fun getAllLocalRecords(): Flow<List<ScoreRecord>> = dao.getAllSortedDesc()

    // Добавление локального рекорда
    // withContext(Dispatchers.IO) — переключаемся на поток ввода-вывода (IO) для выполнения DAO-операции,
    // чтобы не блокировать главный (UI) поток.
    suspend fun insertLocalRecord(record: ScoreRecord) = withContext(Dispatchers.IO) {
        dao.insert(record)
    }

    // Получение топ 5 рекордов из сети
    suspend fun fetchTop5FromNetwork(): List<RemoteRecord> = withContext(Dispatchers.IO) {
        val posts = api.getPosts() // Вызываем suspend-метод Retrofit (getPosts) — сетевой запрос, возвращает List<PostDto>.
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // Создаём форматтер дат: строки вида "2025-10-22" Locale.getDefault() - локальные региональные настройки (язык+формат чисел+страна), в данном случае на вывод не влияет
        val currentDate = formatter.format(Date()) // Получаем текущую дату (Date()) и форматируем её в строку currentDate

        // Берём первые 5 элементов списка posts и PostDto -> RemoteRecord
        posts.take(5).map { post ->
            RemoteRecord(
                playerName = post.title.take(30), // имя игрока - заголовок поста (первые 30 символов)
                score = post.id * 10, // счет - id поста
                date = currentDate // дата текущая
            )
        }
    }
}
