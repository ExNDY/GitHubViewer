package app.thirtyninth.githubviewer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.ExceptionBundle
import app.thirtyninth.githubviewer.data.network.UnauthorizedException
import app.thirtyninth.githubviewer.data.repository.AppRepository
import app.thirtyninth.githubviewer.preferences.KeyValueStorage
import app.thirtyninth.githubviewer.ui.interfaces.LocalizeString
import app.thirtyninth.githubviewer.utils.ValidationResult
import app.thirtyninth.githubviewer.utils.Validator
import app.thirtyninth.githubviewer.utils.mapExceptionToBundle
import app.thirtyninth.githubviewer.utils.mapTokenValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AppRepository,
    private val keyValueStorage: KeyValueStorage
) : ViewModel() {
    private val TAG = AuthViewModel::class.java.simpleName

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val state: StateFlow<ScreenState> = _state

    val authTokenFlow = MutableStateFlow("")

    private fun signInGitHubAndStoreLoginData(authToken: String) {
        val validStatus = validateAuthToken(authToken)

        if (validStatus == ValidationResult.Correct) {
            viewModelScope.launch {
                _state.value = ScreenState.Loading

                val userDataResult = repository.getUserInfo(authToken)

                userDataResult.onSuccess { owner ->
                    Timber.tag(TAG).d(owner.toString())

                    _state.value = ScreenState.Loaded

                    keyValueStorage.saveToken(authToken)

                    _actions.send(Action.RouteToRepositoryList)
                }.onFailure { throwable ->
                    Timber.tag(TAG).d(throwable)

                    _state.value = ScreenState.Loaded

                    if (throwable is UnauthorizedException) {
                        _state.value = ScreenState.InvalidAuthTokenInput(
                            mapTokenValidation(
                                ValidationResult.Incorrect
                            )
                        )
                    } else {
                        _actions.send(Action.ShowError(mapExceptionToBundle(throwable)))
                    }
                }
            }
        } else {
            _state.value = ScreenState.InvalidAuthTokenInput(mapTokenValidation(validStatus))
        }
    }

    fun onSignInButtonPressed() {
        signInGitHubAndStoreLoginData(authTokenFlow.value)
    }

    private fun validateAuthToken(authToken: String) =
        Validator.validateAuthorisationToken(authToken)

    sealed interface ScreenState {
        object Idle : ScreenState
        object Loading : ScreenState
        object Loaded : ScreenState
        data class InvalidAuthTokenInput(val reason: LocalizeString) : ScreenState
    }

    sealed interface Action {
        data class ShowError(val exceptionBundle: ExceptionBundle) : Action
        object RouteToRepositoryList : Action
    }
}