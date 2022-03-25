package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.network.EmptyDataException
import app.thirtyninth.githubviewer.data.network.NoInternetException
import app.thirtyninth.githubviewer.data.repository.AppRepository
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.utils.Variables
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

    private val _repositoryList = MutableSharedFlow<List<GitHubRepository>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val repositoryList: SharedFlow<List<GitHubRepository>> = _repositoryList.asSharedFlow()

    private val _isLoggedIn = MutableSharedFlow<Boolean>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val isLoggedIn: SharedFlow<Boolean> = _isLoggedIn.asSharedFlow()

    private val _actions = MutableSharedFlow<Action>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    init {
        //TODO Двойная обработка, найти способ изящнее
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
        _actions.tryEmit(Action.RouteToAuthScreen)
    }

    private fun getUserRepositoryList() = viewModelScope.launch {
        if (Variables.isNetworkConnected) {
            _actions.tryEmit(Action.SetLoadingStateAction)

            val repositoryListResult = repository.getRepositoryList()

            repositoryListResult.onSuccess { list ->
                if (list != null) {
                    _repositoryList.tryEmit(list)
                    _actions.tryEmit(Action.SetNormalStateAction)
                } else {
                    _actions.tryEmit(Action.ShowErrorAction(EmptyDataException()))
                }
            }.onFailure { throwable ->
                _actions.tryEmit(Action.ShowErrorAction(throwable))
            }
        } else {
            _actions.tryEmit(Action.ShowErrorAction(NoInternetException()))
        }
    }

    sealed interface Action {
        data class ShowToastAction(val message: String) : Action
        data class ShowErrorAction(val exception: Throwable) : Action
        object SetNormalStateAction : Action
        object SetLoadingStateAction : Action
        object RouteToAuthScreen : Action
    }
}