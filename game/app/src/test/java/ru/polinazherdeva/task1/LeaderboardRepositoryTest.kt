package ru.polinazherdeva.task1.repository

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.polinazherdeva.task1.data.ScoreRecordDao
import ru.polinazherdeva.task1.data.ScoreRecord
import ru.polinazherdeva.task1.network.RemoteApi
import ru.polinazherdeva.task1.network.PostDto
import ru.polinazherdeva.task1.network.RemoteRecord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeDao(private val list: MutableList<ScoreRecord> = mutableListOf()) : ScoreRecordDao {
    override suspend fun insert(record: ScoreRecord): Long {
        list.add(record.copy(id = (list.size + 1).toLong()))
        return list.last().id
    }
    override fun getAllSortedDesc() = flowOf(list.sortedByDescending { it.score })
    override suspend fun clearAll() { list.clear() }
}

class FakeApi : RemoteApi {
    override suspend fun getPosts(): List<PostDto> {
        return (1..10).map { PostDto(userId = 1, id = it, title = "Player$it", body = "") }
    }
}

class LeaderboardRepositoryTest {

    @Test
    fun fetchTop5FromNetwork_mapsToRemoteRecord() = runBlocking {
        val dao = FakeDao()
        val api = FakeApi()
        val repo = LeaderboardRepository(dao, api)
        val top5 = repo.fetchTop5FromNetwork()
        assertEquals(5, top5.size)
        assertEquals("Player1", top5[0].playerName)
    }
}
