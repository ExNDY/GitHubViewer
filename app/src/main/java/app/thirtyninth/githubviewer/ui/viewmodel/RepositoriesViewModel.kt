package app.thirtyninth.githubviewer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.common.ServerResponseConstants
import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.network.Result
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.repository.Repository
import app.thirtyninth.githubviewer.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val repository: Repository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private var token: String = ""

    private val _repositoryList = MutableSharedFlow<List<GitHubRepositoryModel>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val repositoryList: SharedFlow<List<GitHubRepositoryModel>> = _repositoryList.asSharedFlow()

    private val _isLoggedIn = MutableSharedFlow<Boolean>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val isLoggedIn: SharedFlow<Boolean> = _isLoggedIn.asSharedFlow()

    private val _uiState = MutableStateFlow(UIState.NORMAL)
    val uiState: StateFlow<UIState> get() = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage.asStateFlow()

    private val _errorFlow = MutableStateFlow(-13)
    val errorFlow: StateFlow<Int> get() = _errorFlow.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.getLoggedInState().onEach {
                _isLoggedIn.tryEmit(it)
            }.collect()
        }

        viewModelScope.launch {
            userPreferences.getLoginData().onEach {
                if (it != null) {
                    token = it.token

                    getUserRepositoryList(token)
                }
            }.catch { exc ->
                println(exc)
            }.collect()
        }
    }

    fun loadData() = viewModelScope.launch {
        getUserRepositoryList(token)
    }

    fun logout() = viewModelScope.launch {
        userPreferences.logout()
    }

    private fun getUserRepositoryList(token: String) = viewModelScope.launch {
        _uiState.tryEmit(UIState.LOADING)

        when (val result = repository.getUserRepository(token)) {
            is Result.Success -> {
                val repoList = result.data

                if (repoList != null) {
                    _repositoryList.tryEmit(repoList)
                    _uiState.tryEmit(UIState.NORMAL)
                } else {
                    _uiState.tryEmit(UIState.ERROR)
                    _errorFlow.tryEmit(-1)
                    _errorMessage.tryEmit("Error: Data is empty")
                }
            }
            is Result.Error -> {
                var errorCode: Int = result.code ?: 0

                // FIXME ненадежно, уже писал как надо делать
                if (result.exception.message == ServerResponseConstants.SERVER_NOT_AVAILABLE) {
                    errorCode = -1
                }

                _errorFlow.tryEmit(errorCode)
                _errorMessage.tryEmit(result.exception.message.toString())
                _uiState.tryEmit(UIState.ERROR)
            }
        }
    }
}