package com.dicoding.asclepius.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.repository.HistoryRepository

class HistoryViewModel(private val repository: HistoryRepository): ViewModel() {

    fun insert(history: History) {
        repository.insert(history)
    }

    fun getAllHistory(): LiveData<List<History>> {
        return repository.getAllHistory()
    }
}