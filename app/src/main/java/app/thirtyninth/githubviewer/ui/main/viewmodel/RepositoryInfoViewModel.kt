package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.models.Readme
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(
    private val repository: GitHubViewerRepository,
    private val userPreferences: UserPreferences,
    state: SavedStateHandle
) : ViewModel() {

    private val _repositoryInfo = MutableSharedFlow<GitHubRepositoryModel>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val repositoryInfo: SharedFlow<GitHubRepositoryModel> = _repositoryInfo.asSharedFlow()

    private val _readme = MutableSharedFlow<Readme?>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val readme: SharedFlow<Readme?> = _readme.asSharedFlow()

    private val _readmeFile = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val readmeFile: SharedFlow<String> = _readmeFile.asSharedFlow()

    private val _uiState = MutableStateFlow(UIState.NORMAL)
    val uiState: StateFlow<UIState> get() = _uiState

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage.asStateFlow()

    private val _errorFlow = MutableSharedFlow<NetworkExceptionType>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val errorFlow: SharedFlow<NetworkExceptionType> = _errorFlow.asSharedFlow()

    private val currentOwner: String = state.get<String>("login").toString()
    private val currentRepositoryName: String = state.get<String>("repository_name").toString()

    init {
        loadRepositoryInfo()
    }

    fun loadRepositoryInfo() = viewModelScope.launch {
        getRepositoryInfo(currentOwner, currentRepositoryName)
        getReadmeInfo(currentOwner, currentRepositoryName)
    }

    private fun getReadmeInfo(owner: String, repositoryName: String) = viewModelScope.launch {
        if (Variables.isNetworkConnected) {
            val readmeData = repository.getReadmeData(owner, repositoryName)

            readmeData.onSuccess { readme ->
                if (readme != null) {
                    _readme.tryEmit(readme)
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
            _readme.tryEmit(null)
        }
    }

    private fun getRepositoryInfo(owner: String, repositoryName: String) =
        viewModelScope.launch {
            if (Variables.isNetworkConnected) {
                _uiState.tryEmit(UIState.LOADING)

                val repositoryDetail = repository.getRepositoryInfo(owner, repositoryName)

                repositoryDetail.onSuccess { repo ->
                    if (repo != null) {
                        _repositoryInfo.tryEmit(repo)
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

    fun logout() = viewModelScope.launch {
        userPreferences.logout()
    }
}