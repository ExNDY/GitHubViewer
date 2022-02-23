package app.thirtyninth.githubviewer.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.network.Result
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.repository.Repository
import app.thirtyninth.githubviewer.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(
    private val repository: Repository,
    private val userPreferences: UserPreferences,
    state: SavedStateHandle
) : ViewModel() {

    private val _repositoryInfo = MutableSharedFlow<GitHubRepositoryModel>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val repositoryInfo: SharedFlow<GitHubRepositoryModel> = _repositoryInfo.asSharedFlow()

    private val _uiState = MutableStateFlow(UIState.NORMAL)
    val uiState: StateFlow<UIState> get() = _uiState

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage.asStateFlow()

    private val currentUsername:String = state.get<String>("username").toString()
    private val currentRepositoryName:String = state.get<String>("repository_name").toString()

    init {
        viewModelScope.launch {
            loadRepositoryInfo()
        }
    }

    fun loadRepositoryInfo() = viewModelScope.launch{
        userPreferences.getAuthenticationToken().onEach {
            if (it != null){
                getRepositoryInfo(it, currentUsername, currentRepositoryName)
            }
        }.collect()
    }

    private fun getRepositoryInfo(token: String, username:String, repositoryName:String) = viewModelScope.launch {
        _uiState.tryEmit(UIState.LOADING)

        when (val result = repository.getRepositoryInfo(token, username, repositoryName)) {
            is Result.Success -> {
                val repo = result.data

                if (repo != null) {
                    _repositoryInfo.tryEmit(repo)
                    _uiState.tryEmit(UIState.NORMAL)
                } else {
                    _uiState.tryEmit(UIState.ERROR)
                    _errorMessage.tryEmit("Error: Data is empty")
                }
            }
            is Result.Error -> {
                _errorMessage.tryEmit(result.exception.message.toString())
                _uiState.tryEmit(UIState.ERROR)
            }
        }
    }

    fun logout() = viewModelScope.launch{
        userPreferences.logout()
    }
}