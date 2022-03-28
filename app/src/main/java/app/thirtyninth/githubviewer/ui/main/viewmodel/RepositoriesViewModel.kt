package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.repository.AppRepository
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.ui.interfaces.LocalizeString
import app.thirtyninth.githubviewer.utils.mapExceptionToStringMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val repository: AppRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isLoggedIn = MutableSharedFlow<Boolean>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val isLoggedIn: SharedFlow<Boolean> = _isLoggedIn.asSharedFlow()

    private val _actions = MutableSharedFlow<Action>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    private val _state = MutableSharedFlow<RepositoriesListScreenState>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val state: SharedFlow<RepositoriesListScreenState> = _state.asSharedFlow()

    init {
        //TODO Унести логику в активити
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

    fun onLogoutClicked(){
        logout()
    }

    private fun logout() = viewModelScope.launch {
        userPreferences.logout()
        _actions.tryEmit(Action.RouteToAuthScreen)
    }

    private fun getUserRepositoryList() = viewModelScope.launch {
        _state.tryEmit(RepositoriesListScreenState.Loading)

        val repositoryListResult = repository.getRepositoryList()

        repositoryListResult.onSuccess { list ->
            _state.tryEmit(RepositoriesListScreenState.Loaded(list))
        }.onFailure { throwable ->
            _state.tryEmit(RepositoriesListScreenState.Error(mapExceptionToStringMessage(throwable)))
        }
    }

    sealed interface RepositoriesListScreenState{
        object Loading : RepositoriesListScreenState
        data class Loaded(val repos: List<GitHubRepository>) : RepositoriesListScreenState
        data class Error(val error:LocalizeString) : RepositoriesListScreenState
        object Empty : RepositoriesListScreenState
    }

    sealed interface Action {
        object RouteToAuthScreen : Action
    }
}