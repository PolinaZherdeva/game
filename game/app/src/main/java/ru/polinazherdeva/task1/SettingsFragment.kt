package ru.polinazherdeva.task1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    // Фрагмент для настроек (музыка, имя игрока и т.д.)

    private lateinit var musicSwitch: Switch
    // lateinit — объявляем переменную, инициализируем её позже в onCreateView.
    private lateinit var playerNameEditText: EditText
    // Поле для ввода имени игрока.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // onCreateView — создаёт и возвращает View иерархию фрагмента.
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        // "Надуваем" XML layout fragment_settings в реальный объект View (корень layout).

        musicSwitch = view.findViewById(R.id.switchMusic)
        // Находим Switch по id в иерархии view (tv, switch и т.д.).
        playerNameEditText = view.findViewById(R.id.etPlayerName)
        // Находим EditText для ввода имени игрока.

        val prefs = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        // Получаем SharedPreferences под именем "game_prefs".
        // MODE_PRIVATE — файл доступен только этому приложению.

        val isMusicOn = prefs.getBoolean("music_on", false)
        // Читаем булево значение (включена музыка или нет). По умолчанию false.
        val playerName = prefs.getString("player_name", "Игрок")
        // Читаем ранее сохранённое имя игрока, по умолчанию "Игрок".

        musicSwitch.isChecked = isMusicOn
        // Устанавливаем состояние переключателя в соответствии с настройкой.
        playerNameEditText.setText(playerName)
        // Устанавливаем текст в поле ввода имени.

        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Слушатель на изменение состояния переключателя.
            prefs.edit().putBoolean("music_on", isChecked).apply()
            // Сохраняем новое значение в SharedPreferences.
            // apply() — асинхронно сохраняет изменения в файл.
        }

        playerNameEditText.setOnFocusChangeListener { _, hasFocus ->
            // Слушатель изменения фокуса у EditText.
            if (!hasFocus) {
                // Когда поле потеряло фокус (пользователь закончил ввод и переключился куда-то),
                // сохраняем текущее значение в SharedPreferences.
                prefs.edit().putString("player_name", playerNameEditText.text.toString()).apply()
            }
        }

        return view
        // Возвращаем готовый View, который система добавит в Activity.
    }
}
