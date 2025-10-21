package ru.polinazherdeva.task1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.polinazherdeva.task1.data.ScoreRecord
import ru.polinazherdeva.task1.data.ScoreRecordDao

// @Database — аннотация Room для базы данных
// entities = [ScoreRecord::class] — таблица, которую будет хранить база
// version = 1 — версия базы
// exportSchema = false — не сохраняем схему в файл
@Database(entities = [ScoreRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // метод для доступа к DAO, который возвращает DAO (ScoreRecordDao)
    // через DAO выполняются вставка, удаление, запросы к таблице
    abstract fun scoreRecordDao(): ScoreRecordDao

    // companion object - статическая часть класса
    companion object {
        // @Volatile — переменная видна всем потокам, гарантирует что все потоки видят одно и то же значение INSTANCE
        // INSTANCE — синглтон базы данных, чтобы не создавать несколько объектов
        @Volatile private var INSTANCE: AppDatabase? = null

        // Функция для получения единственного экземпляра базы, используется Context для доступа к файлу базы
        fun getInstance(context: Context): AppDatabase =
            // если INSTANCE уже создан, возвращаем его
            // иначе создаем объект базы
            // synchronized(this) — блокировка для потокобезопасного создания базы
            // нужно, чтобы два потока не создали два объекта одновременно
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        // функция создания бд
        private fun buildDatabase(context: Context) =
            // Room.databaseBuilder — строит объект базы данных
            // context.applicationContext — используем контекст приложения, чтобы база жила долго
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "leaderboard.db")
                .fallbackToDestructiveMigrationOnDowngrade() // если версия базы упала, старая база удаляется и создаётся новая
                .build() // строим готовый объект бд, возвращает AppDatabase, который можно использовать для получения DAO и работы с таблицей
    }
}