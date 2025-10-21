package ru.polinazherdeva.task1.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.polinazherdeva.task1.data.ScoreRecord
import ru.polinazherdeva.task1.network.RemoteRecord
import ru.polinazherdeva.task1.repository.LeaderboardRepository

class LeaderboardViewModel(private val repo: LeaderboardRepository) : ViewModel() {

    val localRecords: LiveData<List<ScoreRecord>> = repo.getAllLocalRecords().asLiveData()

    private val _remoteTop5 = MutableLiveData<List<RemoteRecord>>()
    val remoteTop5: LiveData<List<RemoteRecord>> = _remoteTop5

    fun loadTop5FromNetwork() {
        viewModelScope.launch {
            try {
                _remoteTop5.value = repo.fetchTop5FromNetwork()
            } catch (e: Exception) {
                _remoteTop5.value = emptyList()
            }
        }
    }

    fun addLocalRecord(playerName: String, score: Int, date: String) {
        viewModelScope.launch {
            repo.insertLocalRecord(ScoreRecord(playerName = playerName, score = score, date = date))
        }
    }
}

class LeaderboardViewModelFactory(private val repo: LeaderboardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LeaderboardViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
