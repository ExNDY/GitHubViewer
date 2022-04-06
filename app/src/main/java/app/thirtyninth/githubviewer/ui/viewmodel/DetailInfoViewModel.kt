package app.thirtyninth.githubviewer.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.ExceptionBundle
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.models.Readme
import app.thirtyninth.githubviewer.data.network.NotFoundException
import app.thirtyninth.githubviewer.data.repository.AppRepository
import app.thirtyninth.githubviewer.preferences.KeyValueStorage
import app.thirtyninth.githubviewer.utils.mapExceptionToBundle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailInfoViewModel @Inject constructor(
    private val repository: AppRepository,
    private val keyValueStorage: KeyValueStorage,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val TAG = DetailInfoViewModel::class.java.simpleName

    private val _actions = MutableSharedFlow<Action>()
    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val state: StateFlow<ScreenState> = _state

    private val _readmeState = MutableStateFlow<ReadmeState>(ReadmeState.Loading)
    val readmeState: StateFlow<ReadmeState> = _readmeState

    private val currentOwner: String = savedStateHandle.get<String>("owner").toString()
    private val currentRepoName: String =
        savedStateHandle.get<String>("repository_name").toString()
    private var readmeData: Readme? = null

    init {
        loadDetailInfo()
    }

    fun retryButtonClicked() {
        loadDetailInfo()
    }

    fun retryReadmeButtonClicked() {
        if (readmeData == null) {
            _readmeState.value = ReadmeState.Empty
        } else {
            fetchReadmeMd(readmeData!!)
        }
    }

    private fun loadDetailInfo() = viewModelScope.launch {
        fetchRepositoryDetails(currentOwner, currentRepoName)
    }

    private fun fetchReadmeMd(readmeDetail: Readme) = viewModelScope.launch {
        _readmeState.value = ReadmeState.Loading

        val result = repository.getReadmeMd(readmeDetail.downloadUrl)

        result.onSuccess { readme ->
            Timber.tag(TAG).d(readme)

            _readmeState.value = ReadmeState.Loaded(readme, readmeDetail)
        }.onFailure { throwable ->
            Timber.tag(TAG).d(throwable)

            _readmeState.value = ReadmeState.Error(mapExceptionToBundle(throwable))
        }
    }

    private fun fetchRepositoryDetails(owner: String, repoName: String) = viewModelScope.launch {
        _state.value = ScreenState.Loading

        val resultDetails = async { repository.getRepositoryDetails(owner, repoName) }
        val resultReadmeDetails = async { repository.getReadmeDetail(owner, repoName) }

        resultDetails.await().onSuccess { repo ->
            Timber.tag(TAG).d(repo.toString())

            _state.value = ScreenState.Loaded(repo, ReadmeState.Loading)
        }.onFailure { throwable ->
            Timber.tag(TAG).d(throwable)

            _state.value = ScreenState.Error(mapExceptionToBundle(throwable))
        }

        resultReadmeDetails.await().onSuccess { readmeDetail ->
            Timber.tag(TAG).d(readmeDetail.toString())

            readmeData = readmeDetail
            fetchReadmeMd(readmeDetail)
        }.onFailure { throwable ->
            Timber.tag(TAG).d(throwable)

            if (throwable is NotFoundException) {
                _readmeState.value = ReadmeState.Empty
            } else {
                _readmeState.value = ReadmeState.Error(mapExceptionToBundle(throwable))
            }
        }
    }

    fun onLogoutClicked() = viewModelScope.launch {
        keyValueStorage.logout()
        _actions.tryEmit(Action.RouteToAuthScreen)
    }

    sealed interface ScreenState {
        object Loading : ScreenState
        data class Error(val exceptionBundle: ExceptionBundle) : ScreenState
        data class Loaded(
            val githubRepo: GitHubRepository,
            val readmeState: ReadmeState
        ) : ScreenState
    }

    sealed interface ReadmeState {
        object Loading : ReadmeState
        object Empty : ReadmeState
        data class Error(val exceptionBundle: ExceptionBundle) : ReadmeState
        data class Loaded(val markdown: String, val readmeDetail: Readme) : ReadmeState
    }

    sealed interface Action {
        object RouteToAuthScreen : Action
    }
}