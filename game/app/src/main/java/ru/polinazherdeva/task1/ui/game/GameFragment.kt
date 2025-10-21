package ru.polinazherdeva.task1.ui.game

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.polinazherdeva.task1.level.Level
import ru.polinazherdeva.task1.R
import ru.polinazherdeva.task1.SharedViewModel
import ru.polinazherdeva.task1.audio.SoundManager
import ru.polinazherdeva.task1.data.AppDatabase
import ru.polinazherdeva.task1.data.ScoreRecord
import ru.polinazherdeva.task1.service.ScoreService

// Фрагмент игры, отображает GameView и кнопки управления
class GameFragment : Fragment(R.layout.fragment_game) {

    // Менеджер звуков (фон и эффекты)
    private var soundManager: SoundManager? = null
    // Основное игровое поле
    private lateinit var gameView: GameView
    // Кнопки управления
    private lateinit var btnPause: Button
    private lateinit var btnRestart: Button
    private lateinit var btnExit: Button

    // Получаем общий ViewModel, где хранится выбранный уровень
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var currentLevel: Level? = null
    private var isMusicOn = true  // включена ли музыка по настройкам

    // Метод вызывается, когда view фрагмента создана
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Находим все view по ID
        gameView = view.findViewById(R.id.gameView)
        btnPause = view.findViewById(R.id.btnPause)
        btnRestart = view.findViewById(R.id.btnRestart)
        btnExit = view.findViewById(R.id.btnExitGame)

        // Получаем настройки из SharedPreferences
        val prefs = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        isMusicOn = prefs.getBoolean("music_on", true) // музыка включена/выключена

        // Получаем выбранный уровень из SharedViewModel
        currentLevel = sharedViewModel.selectedLevel.value
        val levelNumber = currentLevel?.id ?: 1  // если уровень не выбран, используем 1
        gameView.level = levelNumber  // передаём уровень в GameView

        // Инициализируем звуки
        soundManager = SoundManager(requireContext())
        soundManager?.loadEffects(R.raw.eat_sound) // загружаем эффект еды сыра
        if (isMusicOn) soundManager?.startBackground(R.raw.bg_music) // запускаем фоновую музыку

        // Загружаем картинки для мыши, кота и сыра и передаём их в GameView
        val mouseBmp = BitmapFactory.decodeResource(resources, R.drawable.mouse_img)
        val catBmp = BitmapFactory.decodeResource(resources, R.drawable.cat_img)
        val cheeseBmp = BitmapFactory.decodeResource(resources, R.drawable.cheese_img)
        gameView.setBitmaps(mouseBmp, catBmp, cheeseBmp)

        // Запускаем игру, когда view готова (post откладывает выполнение)
        gameView.post { gameView.startGame() }

        // Обработка кнопки Pause/Resume
        btnPause.setOnClickListener {
            if (gameView.isPaused) {
                // если игра на паузе — продолжаем
                gameView.resumeGame()
                btnPause.text = "Пауза"
                if (isMusicOn) soundManager?.startBackground(R.raw.bg_music)
            } else {
                // ставим игру на паузу
                gameView.pauseGame()
                btnPause.text = "Продолжить"
                soundManager?.stopBackground()
            }
        }

        // Кнопка Restart — перезапуск игры
        btnRestart.setOnClickListener {
            gameView.post { gameView.startGame() } // стартуем игру заново
            btnRestart.visibility = View.GONE // скрываем кнопку restart
            btnPause.text = "Пауза"
            if (isMusicOn) soundManager?.startBackground(R.raw.bg_music)
        }

        // Кнопка Exit — выход из игры
        btnExit.setOnClickListener {
            gameView.stopGame()          // останавливаем игру
            soundManager?.stopBackground() // останавливаем музыку
            requireActivity().onBackPressedDispatcher.onBackPressed() // возвращаемся назад
        }

        // Callback, когда мышь съедает сыр
        gameView.onCheeseEaten = { s ->
            soundManager?.playEffect(R.raw.eat_sound) // воспроизводим звук
            val intent = Intent(requireContext(), ScoreService::class.java).apply {
                putExtra("score", s) // передаем score в сервис
            }
            requireContext().startService(intent) // запускаем сервис (foreground service)
        }

        // Callback, когда игра заканчивается
        gameView.onGameOver = { finalScore ->
            btnRestart.visibility = View.VISIBLE // показываем кнопку рестарта
            btnPause.text = "Пауза"
            soundManager?.stopBackground() // останавливаем музыку

            // Диалог ввода имени игрока и сохранения рекорда
            val dlg = GameOverDialogFragment.newInstance(finalScore) { playerName, save ->
                if (save) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        // Сохраняем результат в локальную базу
                        val db = AppDatabase.getInstance(requireContext())
                        db.scoreRecordDao().insert(
                            ScoreRecord(
                                playerName = playerName.ifBlank { "Игрок" }, // если имя пустое — "Игрок"
                                score = finalScore,
                                date = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date()) // дата
                            )
                        )
                    }
                }
            }
            dlg.show(childFragmentManager, "GameOverDialog")
        }
    }

    // Метод вызывается, когда view уничтожается
    override fun onDestroyView() {
        super.onDestroyView()
        gameView.stopGame()      // останавливаем игру
        soundManager?.release()  // освобождаем ресурсы звуков
    }
}