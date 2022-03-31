package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.ExceptionBundle
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.repository.AppRepository
import app.thirtyninth.githubviewer.preferences.KeyValueStorage
import app.thirtyninth.githubviewer.utils.mapExceptionToBundle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val repository: AppRepository,
    private val keyValueStorage: KeyValueStorage
) : ViewModel() {
    private val _actions = MutableSharedFlow<Action>(
        replay = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    private val _state =
        MutableStateFlow<ScreenState>(ScreenState.Loading)
    val state: StateFlow<ScreenState> = _state

    init {
        getUserRepositoryList()
    }

    fun loadData() = viewModelScope.launch {
        getUserRepositoryList()
    }

    fun onRetryClicked() {
        loadData()
    }

    fun onLogoutClicked() {
        logout()
    }

    private fun logout() = viewModelScope.launch {
        keyValueStorage.logout()
        _actions.tryEmit(Action.RouteToAuthScreen)
    }

    private fun getUserRepositoryList() = viewModelScope.launch {
        _state.value = ScreenState.Loading

        val repositoryListResult = repository.getRepositoryList()

        repositoryListResult.onSuccess { list ->
            _state.value = ScreenState.Loaded(list)
        }.onFailure { throwable ->
            _state.value = ScreenState.Error(mapExceptionToBundle(throwable))
        }
    }

    sealed interface ScreenState {
        object Loading : ScreenState
        data class Loaded(val repos: List<GitHubRepository>) : ScreenState
        data class Error(val exceptionBundle: ExceptionBundle) : ScreenState
    }

    sealed interface Action {
        object RouteToAuthScreen : Action
    }
}