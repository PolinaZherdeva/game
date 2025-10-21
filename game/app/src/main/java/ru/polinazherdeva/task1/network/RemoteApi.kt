package ru.polinazherdeva.task1.network

import retrofit2.http.GET // // Импорт аннотации @GET из Retrofit (это библиотека для Android для упрощения работы с сетью) — используется для описания HTTP GET-запроса в интерфейсе API

// interface — это описание поведения, а не сам объект
// Интерфейс описывает что должно происходить, а Retrofit сам создаёт реализацию
interface RemoteApi {
    // Аннотация Retrofit: этот метод выполнит HTTP GET запрос к относительному пути "posts".
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
    // suspend — функция корутины (может выполняться асинхронно без блокировки потока)
    // Возвращает список объектов PostDto, полученных из ответа сервера.
    // Retrofit автоматически выполняет сетевой вызов в фоне и превращает строку JSON в объект PostDto.
}

data class PostDto(
    val userId: Int, // ID пользователя, который создал пост
    val id: Int, // id поста
    val title: String, // заголовок поста
    val body: String // содержание
)

// преобразуем (map) посты API в записи RemoteRecord (playerName, score, date).
// то есть из PostDto делаются объекты RemoteRecord
// jsonplaceholder.typicode.com — это тестовый сервер, а не настоящий игровой API. Он просто возвращает фейковые посты
data class RemoteRecord(
    val playerName: String, // имя игрока
    val score: Int, // очки
    val date: String // дата
)