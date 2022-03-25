package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.models.Readme
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailInfoViewModel @Inject constructor(
    private val repository: AppRepository,
    private val userPreferences: UserPreferences,
    state: SavedStateHandle
) : ViewModel() {

    private val _repositoryInfo = MutableSharedFlow<GitHubRepository>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val repositoryInfo: SharedFlow<GitHubRepository> = _repositoryInfo.asSharedFlow()

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

    private val _actions = MutableSharedFlow<Action>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    private val currentOwner: String = state.get<String>("login").toString()
    private val currentRepositoryName: String = state.get<String>("repository_name").toString()

    init {
        loadRepositoryInfo()
    }

    fun loadRepositoryInfo() = viewModelScope.launch {
        getRepositoryInfo(currentOwner, currentRepositoryName)
        getReadmeInfo(currentOwner, currentRepositoryName)
    }

    //TODO Readme.md reasearch
    private fun getReadmeInfo(owner: String, repositoryName: String) = viewModelScope.launch {
        if (Variables.isNetworkConnected) {
            val readmeData = repository.getReadmeData(owner, repositoryName)

            readmeData.onSuccess { readme ->
                if (readme != null) {
                    _readme.tryEmit(readme)
                }
            }.onFailure { throwable ->
                _actions.tryEmit(Action.ShowErrorAction(throwable))
            }
        } else {
            _readme.tryEmit(null)
        }
    }

    private fun getRepositoryInfo(owner: String, repositoryName: String) =
        viewModelScope.launch {
            if (Variables.isNetworkConnected) {
                _actions.tryEmit(Action.SetLoadingStateAction)

                val repositoryDetail = repository.getRepositoryInfo(owner, repositoryName)

                repositoryDetail.onSuccess { repo ->
                    if (repo != null) {
                        _repositoryInfo.tryEmit(repo)
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

    fun logout() = viewModelScope.launch {
        userPreferences.logout()
        _actions.tryEmit(Action.RouteToAuthScreen)
    }

    sealed interface Action {
        data class ShowToastAction(val message: String) : Action
        data class ShowErrorAction(val exception: Throwable) : Action
        object SetNormalStateAction : Action
        object SetLoadingStateAction : Action
        object RouteToAuthScreen:Action
    }
}