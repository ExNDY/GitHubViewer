package app.thirtyninth.githubviewer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.network.Resource
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val repository: Repository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private var token:String = ""
    private val _repositoryList = MutableSharedFlow<Resource<List<GitHubRepository>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val repositoryList: SharedFlow<Resource<List<GitHubRepository>>> = _repositoryList.asSharedFlow()

    init {
        viewModelScope.launch {
            userPreferences.getAuthenticationToken().onEach {
                if (it != null) {
                    token = it

                    getUserRepositoryList(token)
                }
            }.collect()
        }

    }

    private fun getUserRepositoryList(token:String) = viewModelScope.launch {
        _repositoryList.tryEmit(Resource.loading(null))

        var listData:List<GitHubRepository>? = null

        try {
            listData = repository.getUserRepository(token)
        } catch (ex: Exception){
            _repositoryList.tryEmit(Resource.error(ex.toString(), null))
        }

        if (listData != null){
            _repositoryList.tryEmit(Resource.success(listData))
        } else {
            _repositoryList.tryEmit(Resource.error("Data is empty.", null))
        }

    }


}