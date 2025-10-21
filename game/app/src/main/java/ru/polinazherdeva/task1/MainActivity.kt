package ru.polinazherdeva.task1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Главная активность приложения (первый экран)
class MainActivity : AppCompatActivity() {

    // Метод вызывается при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаем режим Edge-to-Edge — контент заполняет всю область экрана, включая области за системными панелями
        enableEdgeToEdge()

        // "Надуваем" разметку для этой активности
        setContentView(R.layout.activity_main)

        // Настройка отступов под системные панели (статусбар, навигация) через WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // получаем размеры системных панелей
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom) // применяем отступы к корневому view
            insets
        }

        // Находим кнопку "Начать игру" в разметке
        val startButton: Button = findViewById(R.id.startButton)

        // Обработка нажатия на кнопку
        startButton.setOnClickListener {
            // Показываем короткое уведомление (Toast)
            Toast.makeText(this, "Игра начинается!", Toast.LENGTH_SHORT).show()

            // Создаем Intent для перехода на GameActivity
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent) // запускаем GameActivity
        }
    }
}