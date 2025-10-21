package ru.polinazherdeva.task1.ui.game

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.polinazherdeva.task1.R

// Диалоговое окно, которое показывается после окончания игры
class GameOverDialogFragment : DialogFragment() {

    // Статический блок для создания экземпляра фрагмента с параметрами
    companion object {
        private const val ARG_SCORE = "arg_score" // ключ для передачи счета игрока

        // Метод для создания нового фрагмента с параметрами
        fun newInstance(score: Int, onSave: (String, Boolean) -> Unit): GameOverDialogFragment {
            val f = GameOverDialogFragment() // создаем новый экземпляр фрагмента
            f.arguments = Bundle().apply { putInt(ARG_SCORE, score) } // передаем счет через Bundle
            f.saveCallback = onSave // сохраняем callback для обработки результата (имя игрока и флаг сохранения)
            return f
        }
    }

    // Callback, который вызывается после выбора "Сохранить" или "Не сохранять"
    private var saveCallback: ((String, Boolean) -> Unit)? = null

    // Метод, который вызывается при создании диалога
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Получаем счет игрока из аргументов
        val score = requireArguments().getInt(ARG_SCORE)

        // Создаем EditText для ввода имени игрока
        val et = EditText(requireContext()).apply {
            hint = "Имя игрока"      // подсказка в поле ввода
            setText("Игрок")          // начальный текст по умолчанию
        }

        // Создаем AlertDialog с заголовком, сообщением, полем ввода и кнопками
        return AlertDialog.Builder(requireContext())
            .setTitle("Игра окончена") // заголовок диалога
            .setMessage("Ваш счёт: $score\nСохранить рекорд?") // сообщение с текущим счетом
            .setView(et) // добавляем EditText в диалог
            .setPositiveButton("Сохранить") { _, _ ->
                // Нажата кнопка "Сохранить"
                // вызываем callback с именем игрока (если пустое — "Игрок") и флагом true
                saveCallback?.invoke(et.text.toString().ifBlank { "Игрок" }, true)
            }
            .setNegativeButton("Не сохранять") { _, _ ->
                // Нажата кнопка "Не сохранять"
                // вызываем callback с пустой строкой и флагом false
                saveCallback?.invoke("", false)
            }
            .create() // создаем и возвращаем диалог
    }
}