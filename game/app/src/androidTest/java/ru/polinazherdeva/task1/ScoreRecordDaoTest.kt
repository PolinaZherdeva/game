package ru.polinazherdeva.task1

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.polinazherdeva.task1.data.AppDatabase
import ru.polinazherdeva.task1.data.ScoreRecord
import ru.polinazherdeva.task1.data.ScoreRecordDao

class ScoreRecordDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ScoreRecordDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.scoreRecordDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndGetSorted() = runBlocking {
        dao.insert(ScoreRecord(playerName = "A", score = 50, date = "2023-10-01"))
        dao.insert(ScoreRecord(playerName = "B", score = 100, date = "2023-10-02"))
        dao.insert(ScoreRecord(playerName = "C", score = 75, date = "2023-10-03"))

        val list = dao.getAllSortedDesc().first()
        Assert.assertEquals(3, list.size)
        Assert.assertEquals("B", list[0].playerName) // top first
        Assert.assertEquals("C", list[1].playerName)
        Assert.assertEquals("A", list[2].playerName)
    }
}