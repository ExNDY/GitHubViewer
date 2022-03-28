package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailInfoViewModel @Inject constructor(
    private val repository: AppRepository,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _action = MutableSharedFlow<Action>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val action: SharedFlow<Action> = _action.asSharedFlow()

    private val _state = MutableSharedFlow<DetailInfoScreenState>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val state: SharedFlow<DetailInfoScreenState> = _state.asSharedFlow()

    private val _readmeState = MutableSharedFlow<ReadmeState>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val readmeState: SharedFlow<ReadmeState> = _readmeState.asSharedFlow()

    private val currentOwner: String = savedStateHandle.get<String>("owner").toString()
    private val currentRepositoryName: String = savedStateHandle.get<String>("repository_name").toString()

    init {
        loadRepositoryInfo()
    }

    fun retryButtonClicked() {
        loadRepositoryInfo()
    }

    private fun loadRepositoryInfo() = viewModelScope.launch {
        getRepositoryInfo(currentOwner, currentRepositoryName)
        //getReadmeInfo(currentOwner, currentRepositoryName)
    }

    //TODO Readme.md reasearch
//    private fun getReadmeInfo(owner: String, repositoryName: String) = viewModelScope.launch {
//        val readmeData = repository.getReadmeData(owner, repositoryName)
//
//        readmeData.onSuccess { readme ->
//            _readme.tryEmit(readme)
//        }.onFailure { throwable ->
//            _actions.tryEmit(Action.ShowErrorAction(throwable))
//        }
//    }

    private fun getRepositoryInfo(owner: String, repositoryName: String) =
        viewModelScope.launch {
            _state.tryEmit(DetailInfoScreenState.Loading)

            val repositoryDetail = repository.getRepositoryInfo(owner, repositoryName)

            repositoryDetail.onSuccess { repo ->
                _state.tryEmit(DetailInfoScreenState.Loaded(repo, ReadmeState.Empty))
            }.onFailure { throwable ->
                _state.tryEmit(DetailInfoScreenState.Error(mapExceptionToStringMessage(throwable)))
            }
        }

    fun logout() = viewModelScope.launch {
        userPreferences.logout()
        _action.tryEmit(Action.RouteToAuthScreen)
    }

    sealed interface DetailInfoScreenState {
        object Loading : DetailInfoScreenState
        data class Error(val error: LocalizeString) : DetailInfoScreenState
        data class Loaded(
            val githubRepo: GitHubRepository,
            val readmeState: ReadmeState) : DetailInfoScreenState
    }

    sealed interface ReadmeState {
        object Loading : ReadmeState
        object Empty : ReadmeState
        data class Error(val error: LocalizeString) : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }

    sealed interface Action {
        object RouteToAuthScreen : Action
    }
}