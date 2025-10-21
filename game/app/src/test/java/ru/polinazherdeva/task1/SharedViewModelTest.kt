package ru.polinazherdeva.task1

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import ru.polinazherdeva.task1.level.Level

class SharedViewModelTest {

    // ✅ Правило для синхронного выполнения LiveData
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testSelectLevel() {
        val viewModel = SharedViewModel()
        val level = Level(1, "Уровень 1", "Легкий")

        viewModel.selectLevel(level)

        assertEquals(level, viewModel.selectedLevel.value)
    }
}
