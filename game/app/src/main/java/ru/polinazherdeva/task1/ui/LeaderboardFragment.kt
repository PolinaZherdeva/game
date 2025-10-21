package ru.polinazherdeva.task1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.polinazherdeva.task1.data.AppDatabase
import ru.polinazherdeva.task1.R
import ru.polinazherdeva.task1.network.RetrofitClient
import ru.polinazherdeva.task1.repository.LeaderboardRepository
import ru.polinazherdeva.task1.viewmodel.LeaderboardViewModel
import ru.polinazherdeva.task1.viewmodel.LeaderboardViewModelFactory
import androidx.recyclerview.widget.RecyclerView

// Фрагмент для отображения таблицы рекордов (локальные и топ-5 онлайн)
class LeaderboardFragment : Fragment() {

    // Адаптер для локальной таблицы рекордов
    private lateinit var localAdapter: LocalLeaderboardAdapter
    // Адаптер для топ-5 онлайн
    private lateinit var remoteAdapter: RemoteLeaderboardAdapter

    // ViewModel с фабрикой для создания через репозиторий
    private val viewModel: LeaderboardViewModel by viewModels {
        // Получаем доступ к базе данных
        val db = AppDatabase.getInstance(requireContext())
        // Создаем репозиторий с DAO и Retrofit API
        val repo = LeaderboardRepository(db.scoreRecordDao(), RetrofitClient.create())
        // Фабрика ViewModel
        LeaderboardViewModelFactory(repo)
    }

    // Создание view фрагмента
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // "Надуваем" XML макет фрагмента
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        // RecyclerView для локальных рекордов
        val rvLocal = view.findViewById<RecyclerView>(R.id.rvLocal)
        // RecyclerView для топ-5 онлайн
        val rvRemote = view.findViewById<RecyclerView>(R.id.rvRemote)
        // Кнопка для загрузки топ-5 с сервера
        val btnLoad = view.findViewById<Button>(R.id.btnLoadTop5)

        // Инициализация адаптера для локальных рекордов
        localAdapter = LocalLeaderboardAdapter()
        rvLocal.layoutManager = LinearLayoutManager(requireContext()) // вертикальный список
        rvLocal.adapter = localAdapter

        // Инициализация адаптера для топ-5 онлайн
        remoteAdapter = RemoteLeaderboardAdapter()
        rvRemote.layoutManager = LinearLayoutManager(requireContext()) // вертикальный список
        rvRemote.adapter = remoteAdapter

        // Подписка на изменения локальных рекордов
        viewModel.localRecords.observe(viewLifecycleOwner) { localAdapter.submitList(it) }
        // Подписка на изменения топ-5 онлайн
        viewModel.remoteTop5.observe(viewLifecycleOwner) { remoteAdapter.submitList(it) }

        // Нажатие кнопки — загрузка топ-5 рекордов с сервера
        btnLoad.setOnClickListener { viewModel.loadTop5FromNetwork() }

        return view // возвращаем созданный view
    }
}