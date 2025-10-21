package ru.polinazherdeva.task1.level

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment // Fragment — отдельный экран или часть экрана
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager // LinearLayoutManager — указывает, как отображать список (по вертикали)
import androidx.recyclerview.widget.RecyclerView // RecyclerView — список уровней
import ru.polinazherdeva.task1.R
import ru.polinazherdeva.task1.SharedViewModel // SharedViewModel — модель, которая хранит выбранный уровень

// класс для управления экраном, где пользователь выбирает уровень
class LevelsFragment : Fragment() {

    // Получаем общую модель данных (ViewModel), разделяемую между фрагментами активности
    // by activityViewModels() — это делегат, который сам создаёт и “привязывает” SharedViewModel к Activity
    // Через него мы сможем передавать выбранный уровень другим фрагментам
    private val viewModel: SharedViewModel by activityViewModels()

    // onCreateView вызывается, когда фрагмент должен “нарисовать” свой интерфейс.
    // Возвращает корневой View (всё, что будет на экране)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Загружает макет fragment_levels.xml и превращает его в объект View
        val view = inflater.inflate(R.layout.fragment_levels, container, false)

        // Создаём список уровней (три объекта Level)
        // Эти данные мы будем показывать в списке
        val levelsList = listOf(
            Level(1, "Уровень 1", "Легкий"),
            Level(2, "Уровень 2", "Средний"),
            Level(3, "Уровень 3", "Сложный")
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvLevels) // ищем RecyclerView в макете (R.id.rvLevels)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // для отображения вертикального списка
        // Назначаем адаптер LevelsAdapter и передаём: список уровней и лямбда-функцию, которая срабатывает при клике на элемент
        recyclerView.adapter = LevelsAdapter(levelsList) { level ->
            viewModel.selectLevel(level) // вызываем функцию из SharedViewModel ля сохранения уровня в общей модели
            Toast.makeText(requireContext(), "Выбран: ${level.name}", Toast.LENGTH_SHORT).show() // показываем уведомление Toast с текстом “Выбран: Уровень n”

        }

        return view // возвращаем созданный интерфейс, чтобы Android его показал на экране
    }
}