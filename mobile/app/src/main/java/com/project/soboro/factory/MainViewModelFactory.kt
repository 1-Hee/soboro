package com.project.soboro.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.soboro.model.MainViewModel
import com.project.soboro.repository.Repository

class MainViewModelFactory(
    private val repository : Repository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}
