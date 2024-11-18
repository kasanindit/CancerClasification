package com.dicoding.asclepius.repository

import androidx.lifecycle.LiveData
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.database.HistoryDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(private val historyDao: HistoryDao) {
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    fun getAllHistory(): LiveData<List<History>> = historyDao.getAllHistory()

    fun insert(history: History){
        executorService.execute { historyDao.insert(history) }
    }

}