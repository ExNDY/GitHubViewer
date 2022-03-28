package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.LoginData
import app.thirtyninth.githubviewer.data.network.UnauthorizedException
import app.thirtyninth.githubviewer.data.repository.AppRepository
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.ui.interfaces.LocalizeString
import app.thirtyninth.githubviewer.ui.interfaces.ValidationResult
import app.thirtyninth.githubviewer.utils.Validator
import app.thirtyninth.githubviewer.utils.mapExceptionToStringMessage
import app.thirtyninth.githubviewer.utils.mapTokenValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AppRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _actions = MutableSharedFlow<Action>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    private val _state = MutableSharedFlow<AuthScreenState>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val state: SharedFlow<AuthScreenState> = _state.asSharedFlow()

    val authTokenFlow = MutableStateFlow<String>("")

    //TODO не работает
    var authTokenValidation: StateFlow<ValidationResult> = authTokenFlow.map { token ->
        validateAuthToken(token)
    }.stateIn(viewModelScope, SharingStarted.Lazily, ValidationResult.Correct)

    private fun signInGitHubAndStoreLoginData(authToken: String) =
        viewModelScope.launch {
            _state.tryEmit(AuthScreenState.Idle)

            val userDataResult = repository.getUserInfo(authToken)

            userDataResult.onSuccess {
                _state.tryEmit(AuthScreenState.Loaded)

                userPreferences.saveUser(LoginData(authToken))

                _actions.tryEmit(Action.RouteToRepositoryList)
            }.onFailure { throwable ->
                _state.tryEmit(AuthScreenState.Loaded)

                if (throwable is UnauthorizedException) {
                    _state.tryEmit(
                        AuthScreenState.InvalidAuthTokenInput(
                            mapTokenValidation(
                                ValidationResult.Incorrect
                            )
                        )
                    )
                } else {
                    _actions.tryEmit(Action.ShowError(mapExceptionToStringMessage(throwable)))
                }
            }
        }

    fun onSignInButtonPressed() {
        signInGitHubAndStoreLoginData(authTokenFlow.value)
    }

    private fun validateAuthToken(authToken: String) =
        Validator.validateAuthorisationToken(authToken)

    sealed interface AuthScreenState {
        object Idle : AuthScreenState
        object Loaded : AuthScreenState
        data class InvalidAuthTokenInput(val reason: LocalizeString) : AuthScreenState
    }

    sealed interface Action {
        data class ShowError(val error: LocalizeString) : Action
        object RouteToRepositoryList : Action
    }
}