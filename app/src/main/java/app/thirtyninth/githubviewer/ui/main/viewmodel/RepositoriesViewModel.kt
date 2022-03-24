package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.network.HttpCallException
import app.thirtyninth.githubviewer.data.network.NetworkExceptionType
import app.thirtyninth.githubviewer.data.network.NoInternetException
import app.thirtyninth.githubviewer.data.network.UnauthorizedException
import app.thirtyninth.githubviewer.data.repository.GitHubViewerRepository
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.utils.UIState
import app.thirtyninth.githubviewer.utils.Variables
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val repository: GitHubViewerRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

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

    private val _errorFlow = MutableSharedFlow<NetworkExceptionType>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val errorFlow: SharedFlow<NetworkExceptionType> = _errorFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            userPreferences.getLoggedInState().onEach {
                _isLoggedIn.tryEmit(it)
            }.collect()
        }

        getUserRepositoryList()
    }

    fun loadData() = viewModelScope.launch {
        getUserRepositoryList()
    }

    fun logout() = viewModelScope.launch {
        userPreferences.logout()
    }

    private fun getUserRepositoryList() = viewModelScope.launch {
        if (Variables.isNetworkConnected) {
            _uiState.tryEmit(UIState.LOADING)

            val repositoryListResult = repository.getRepositoryList()

            repositoryListResult.onSuccess { list ->
                if (list != null) {
                    _repositoryList.tryEmit(list)
                    _uiState.tryEmit(UIState.NORMAL)
                } else {
                    _uiState.tryEmit(UIState.ERROR)
                    _errorFlow.tryEmit(NetworkExceptionType.EMPTY_DATA)
                    _errorMessage.tryEmit("Error: Data is empty")
                }
            }.onFailure { throwable ->
                when (throwable) {
                    is UnauthorizedException -> {

                    }
                    is NoInternetException -> {

                    }
                    is HttpCallException -> {

                    }
                }
            }
        } else {
            _uiState.tryEmit(UIState.NORMAL)
            _errorFlow.tryEmit(NetworkExceptionType.SERVER_ERROR)
        }
    }
}