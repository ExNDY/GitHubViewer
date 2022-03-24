package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.LoginData
import app.thirtyninth.githubviewer.data.network.EmptyDataException
import app.thirtyninth.githubviewer.data.network.NoInternetException
import app.thirtyninth.githubviewer.data.repository.GitHubViewerRepository
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.utils.LoginState
import app.thirtyninth.githubviewer.utils.TokenState
import app.thirtyninth.githubviewer.utils.UIState
import app.thirtyninth.githubviewer.utils.Validations
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
class LoginViewModel @Inject constructor(
    private val repository: GitHubViewerRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState.NORMAL)
    val uiState: StateFlow<UIState> get() = _uiState.asStateFlow()

    private val _loginValid = MutableStateFlow(LoginState.CORRECT)
    val loginValid: StateFlow<LoginState> get() = _loginValid

    private val _authorisationTokenValid = MutableStateFlow(TokenState.CORRECT)
    val authorisationTokenValid: StateFlow<TokenState> get() = _authorisationTokenValid

    private val _actions = MutableSharedFlow<Action>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    fun signInGitHubAndStoreLoginData(login: String, authToken: String) = viewModelScope.launch {
        if (Variables.isNetworkConnected) {
            _uiState.tryEmit(UIState.LOADING)

            val userDataResult = repository.getUserInfo("token $authToken")

            userDataResult.onSuccess { owner ->
                if (owner != null) {
                    if (owner.login.equals(login, true)) {
                        userPreferences.saveUser(LoginData(login, authToken))

                        _uiState.tryEmit(UIState.NORMAL)
                        _actions.tryEmit(Action.RouteSuccessAction)
                    } else {
                        _uiState.tryEmit(UIState.NORMAL)
                        _loginValid.tryEmit(LoginState.INVALID)
                    }
                } else {
                    _uiState.tryEmit(UIState.NORMAL)
                    _actions.tryEmit(Action.ShowErrorAction(EmptyDataException()))
                }
            }.onFailure { throwable ->
                _actions.tryEmit(Action.ShowErrorAction(throwable))
            }
        } else {
            _uiState.tryEmit(UIState.NORMAL)
            _actions.tryEmit(Action.ShowErrorAction(NoInternetException()))
        }
    }

    fun validateLogin(login: String) = viewModelScope.launch {
        _loginValid.value = Validations.validateLogin(login)
    }

    fun validateToken(authToken: String) = viewModelScope.launch {
        _authorisationTokenValid.value = Validations.validateAuthorisationToken(authToken)
    }

    sealed interface Action {
        data class ShowToastAction(val message: String) : Action
        data class SignInAction(val login: String, val authToken: String) : Action
        data class ShowErrorAction(val exception: Throwable) : Action
        object RouteSuccessAction : Action
    }
}