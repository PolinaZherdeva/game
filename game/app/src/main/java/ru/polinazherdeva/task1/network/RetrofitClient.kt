package ru.polinazherdeva.task1.network

import okhttp3.OkHttpClient // // Импорт клиента OkHttp — HTTP-клиент, который Retrofit использует для отправки запросов и получения ответов
import okhttp3.logging.HttpLoggingInterceptor // Класс, который умеет логировать HTTP-трафик (заголовки, URL, код ответа и т.д.)
import retrofit2.Retrofit // Класс Retrofit — основной строитель API-клиента.Он собирает базовый URL, HTTP-клиент, конвертеры (для JSON) и на их основе создаёт объект,
                          // который будет выполнять HTTP-запросы по описанным интерфейсам.
import retrofit2.converter.gson.GsonConverterFactory // Конвертер для Retrofit: преобразует JSON в Kotlin/Java объекты с помощью Gson (и обратно)

object RetrofitClient {
    // Метод, который создаёт и возвращает реализацию интерфейса RemoteApi
    fun create(): RemoteApi {
        // Создаём экземпляр HttpLoggingInterceptor.
        // .apply { ... } — удобная Kotlin-функция (scope function): возвращает объект после выполнения блока,
        // внутри блока можно настроить объект (здесь устанавливаем уровень логирования).
        // Level.BASIC — логирует основную информацию: HTTP-метод (GET/POST), URL, код ответа, время
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        // Создаём OkHttpClient через Builder:
        // - .addInterceptor(logging) — добавляем наш интерсептор в цепочку интерсепторов.
        //   Интерсепторы перехватывают запросы/ответы: можно логировать, добавлять заголовки, кэшировать и т.д.
        // - .build() — возвращает готовый OkHttpClient с настроенной цепочкой.
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        val retrofit = Retrofit.Builder()
            // Указываем базовый URL. Все пути в аннотациях (например, @GET("posts")) будут присоединяться к нему.
            .baseUrl("https://jsonplaceholder.typicode.com/")
            // Передаём наш кастомный OkHttpClient в Retrofit, чтобы Retrofit использовал его для всех запросов
            .client(client)
            // Добавляем конвертер Gson: он отвечает за преобразование JSON ответа в объекты Kotlin (PostDto)
            .addConverterFactory(GsonConverterFactory.create())
            // Строим объект Retrofit: он теперь знает базовый URL, HTTP-клиент и конвертер
            .build()

        // Возвращаем реализацию интерфейса RemoteApi
        return retrofit.create(RemoteApi::class.java)
    }
}