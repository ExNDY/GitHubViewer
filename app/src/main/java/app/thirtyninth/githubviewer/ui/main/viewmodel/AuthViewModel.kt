package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.ExceptionBundle
import app.thirtyninth.githubviewer.data.models.LoginData
import app.thirtyninth.githubviewer.data.network.UnauthorizedException
import app.thirtyninth.githubviewer.data.repository.AppRepository
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.ui.interfaces.LocalizeString
import app.thirtyninth.githubviewer.ui.interfaces.ValidationResult
import app.thirtyninth.githubviewer.utils.Validator
import app.thirtyninth.githubviewer.utils.mapExceptionToBundle
import app.thirtyninth.githubviewer.utils.mapTokenValidation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AppRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _actions = MutableSharedFlow<Action>()
    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loaded)
    val state: StateFlow<ScreenState> = _state

    val authTokenFlow = MutableStateFlow<String>("")

    private fun signInGitHubAndStoreLoginData(authToken: String) {
        val validStatus = validateAuthToken(authToken)

        if (validStatus is ValidationResult.Correct) {
            viewModelScope.launch {
                _state.value = ScreenState.Idle

                val userDataResult = repository.getUserInfo(authToken)

                userDataResult.onSuccess {
                    _state.value = ScreenState.Loaded

                    userPreferences.saveUser(LoginData(authToken))

                    _actions.tryEmit(Action.RouteToRepositoryList)
                }.onFailure { throwable ->
                    _state.value = ScreenState.Loaded

                    if (throwable is UnauthorizedException) {
                        _state.value = ScreenState.InvalidAuthTokenInput(
                            mapTokenValidation(
                                ValidationResult.Incorrect
                            )
                        )
                    } else {
                        _actions.tryEmit(Action.ShowError(mapExceptionToBundle(throwable)))
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
        object Loaded : ScreenState
        data class InvalidAuthTokenInput(val reason: LocalizeString) : ScreenState
    }

    sealed interface Action {
        data class ShowError(val exceptionBundle: ExceptionBundle) : Action
        object RouteToRepositoryList : Action
    }
}