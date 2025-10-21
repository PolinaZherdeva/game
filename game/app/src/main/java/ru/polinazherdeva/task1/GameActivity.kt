package ru.polinazherdeva.task1

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.polinazherdeva.task1.ui.LeaderboardFragment
import ru.polinazherdeva.task1.ui.game.GameFragment
import ru.polinazherdeva.task1.SettingsFragment
import ru.polinazherdeva.task1.level.LevelsFragment

class GameActivity : AppCompatActivity() {

    private lateinit var btnStartGame: Button // кнопка "Начать игру"
    private lateinit var bottomNav: BottomNavigationView // нижнее меню навигации
    private var isGameActive = false  // отслеживаем, идет ли игра, чтобы блокировать навигацию

    // Метод вызывается при создании Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game) // "надуваем" layout активности

        // Инициализация кнопки и нижнего меню
        btnStartGame = findViewById(R.id.btnStartGame)
        bottomNav = findViewById(R.id.bottomNav)

        // Показываем LevelsFragment по умолчанию
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LevelsFragment())
            .commit()

        // Обработка нажатия кнопки "Начать игру"
        btnStartGame.setOnClickListener {
            startGame()
        }

        // Обработка выбора пунктов нижнего меню
        bottomNav.setOnItemSelectedListener { item ->
            if (isGameActive) return@setOnItemSelectedListener true // блокируем навигацию во время игры

            when (item.itemId) {
                // Показ LevelsFragment
                R.id.nav_levels -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, LevelsFragment())
                        .commit()
                    btnStartGame.visibility = View.VISIBLE // показываем кнопку старта
                    true
                }
                // Показ LeaderboardFragment
                R.id.nav_leaderboard -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, LeaderboardFragment())
                        .commit()
                    btnStartGame.visibility = View.GONE  // скрываем кнопку старта
                    true
                }
                // Показ SettingsFragment
                R.id.nav_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, SettingsFragment())
                        .commit()
                    btnStartGame.visibility = View.GONE // скрываем кнопку старта
                    true
                }
                else -> false
            }
        }

        // Слушатель изменения back stack для отслеживания возврата из игры
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (currentFragment !is GameFragment) {
                // Если текущий фрагмент не игровой, показываем меню и кнопку старта
                bottomNav.visibility = View.VISIBLE
                btnStartGame.visibility = View.VISIBLE
                isGameActive = false
            }
        }
    }

    // Метод запуска игры
    private fun startGame() {
        val fragment = GameFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment) // заменяем текущий фрагмент на GameFragment
            .addToBackStack("game")  // добавляем в back stack, чтобы можно было вернуться
            .commit()

        // Скрываем интерфейс во время игры
        btnStartGame.visibility = View.GONE
        bottomNav.visibility = View.GONE
        isGameActive = true // помечаем, что игра активна
    }

    // Обработка кнопки "Назад"
    @SuppressLint("GestureBackNavigation")
    override fun onBackPressed() {
        if (isGameActive) {
            // Если игра активна, просто закрываем игру, показываем меню
            supportFragmentManager.popBackStack()
            bottomNav.visibility = View.VISIBLE
            btnStartGame.visibility = View.VISIBLE
            isGameActive = false
        } else {
            // Иначе стандартное поведение кнопки "Назад"
            super.onBackPressed()
        }
    }
}
