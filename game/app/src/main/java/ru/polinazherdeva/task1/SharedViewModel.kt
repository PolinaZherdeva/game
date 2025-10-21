package ru.polinazherdeva.task1

import androidx.lifecycle.LiveData // LiveData и MutableLiveData — позволяют отслеживать изменения данных
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel // ViewModel — хранит данные независимо от жизненного цикла фрагментов
import ru.polinazherdeva.task1.level.Level

// Создаём класс модели, который будет жить столько, сколько живёт активность
class SharedViewModel : ViewModel() {

    // _selectedLevel — внутренняя переменная, где хранится выбранный уровень
    // MutableLiveData — “живые” данные, которые можно изменять
    private val _selectedLevel = MutableLiveData<Level?>()

    // Публичная версия LiveData, только для чтения.
    //Фрагменты могут “наблюдать” за изменением уровня, но не изменять напрямую
    val selectedLevel: LiveData<Level?> get() = _selectedLevel

    // Метод, который сохраняет выбранный уровень
    // После вызова все “наблюдатели” (Observers) узнают, что уровень изменился
    fun selectLevel(level: Level) {
        _selectedLevel.value = level
    }
}
