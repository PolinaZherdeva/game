package ru.polinazherdeva.task1.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ru.polinazherdeva.task1.R

// Определяем класс сервиса — наследуем от android.app.Service.
// Сервис выполняется в фоне и может показывать foreground-уведомление.
class ScoreService : Service() {

    // companion object — статический блок, доступный без экземпляра:
    //CHANNEL_ID — уникальный ID для канала уведомлений; нужен Android 8+.
    //NOTIF_ID — ID самого уведомления, чтобы можно было его обновлять (если запустить сервис снова — старое уведомление заменится
    companion object {
        const val CHANNEL_ID = "game_score_channel"
        const val NOTIF_ID = 101
    }

    private var currentScore = 0 // текущий счёт игрока

    // onCreate() вызывается один раз, когда сервис создаётся системой
    override fun onCreate() {
        super.onCreate()
        createChannel() // создаёт канал уведомлений (требуется Android) без канала уведомление не покажется и сервис может упасть
    }

    // Подавление предупреждения Lint, которое уведомляет, что вы запускаете foreground сервис и нужно указать его тип (mediaPlayback, location и т.д.)
    @SuppressLint("ForegroundServiceType")
    // метод жизненного цикла сервиса, вызывается каждый раз, когда кто-то запускает сервис через startService(intent)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // достаём число из Intent с ключом "score"
        intent?.getIntExtra("score", -1)?.let {
            // проверяем значение на корректность (0 или больше)
            if (it >= 0) {
                currentScore = it // Сохраняем его в переменную сервиса currentScore
                startForeground(NOTIF_ID, buildNotification("Score: $currentScore")) // Вызываем startForeground → переводим сервис в foreground, показываем постоянное уведомление с текущим счётом
            }
        }
        return START_STICKY // перезапуск сервиса, если Intent, который его запустил, больше не доступен
    }

    // // Создаём Notification через NotificationCompat для совместимости
    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Кошки-Мышки") // заголовок уведомления
            .setContentText(text) // основной текст уведомления
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build() // build() возвращает готовый Notification
    }

    private fun createChannel() {
        // // Создаём канал уведомлений только на Android O (API 26) и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Получаем менеджер уведомлений из контекста
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // Создаём и регистрируем NotificationChannel
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Game score",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    // onBind используется при привязанных сервисах (bound service). Здесь возвращаем null,
    // значит сервис не предназначен для связывания и используется как стартуемый (startService)
    override fun onBind(intent: Intent?): IBinder? = null
}