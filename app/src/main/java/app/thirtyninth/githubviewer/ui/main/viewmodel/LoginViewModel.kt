package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.LoginData
import app.thirtyninth.githubviewer.data.network.EmptyDataException
import app.thirtyninth.githubviewer.data.network.NoInternetException
import app.thirtyninth.githubviewer.data.network.UnauthorizedException
import app.thirtyninth.githubviewer.data.repository.GitHubViewerRepository
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.ui.interfaces.Validation
import app.thirtyninth.githubviewer.utils.Validations
import app.thirtyninth.githubviewer.utils.Variables
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: GitHubViewerRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _actions = MutableSharedFlow<Action>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val actions: SharedFlow<Action> = _actions.asSharedFlow()

    val loginFlow = MutableStateFlow<String?>(null)
    val authTokenFlow = MutableStateFlow<String?>(null)

    val authTokenErrorStatus = MutableStateFlow<Validation>(Validation.Correct)
    val loginErrorStatus = MutableStateFlow<Validation>(Validation.Correct)

    private fun signInGitHubAndStoreLoginData(login: String, authToken: String) =
        viewModelScope.launch {
            if (Variables.isNetworkConnected) {
                _actions.tryEmit(Action.SetLoadingStateAction)

                val userDataResult = repository.getUserInfo("token $authToken")

                userDataResult.onSuccess { owner ->
                    if (owner != null) {
                        if (owner.login.equals(login, true)) {
                            userPreferences.saveUser(LoginData(login, authToken))

                            _actions.tryEmit(Action.RouteSuccessAction)
                        } else {
                            _actions.tryEmit(Action.SetNormalStateAction)
                            //TODO Найти решение покрасивее
                            loginErrorStatus.tryEmit(Validation.Incorrect)
                            delay(3000)
                            loginErrorStatus.tryEmit(Validation.Correct)
                        }
                    } else {
                        _actions.tryEmit(Action.ShowErrorAction(EmptyDataException()))
                    }
                }.onFailure { throwable ->
                    _actions.tryEmit(Action.SetNormalStateAction)

                    if (throwable is UnauthorizedException) {
                        //TODO Найти решение покрасивее
                        authTokenErrorStatus.tryEmit(Validation.Incorrect)
                        delay(3000)
                        authTokenErrorStatus.tryEmit(Validation.Correct)
                    } else {
                        _actions.tryEmit(Action.ShowErrorAction(throwable))
                    }
                }
            } else {
                _actions.tryEmit(Action.ShowErrorAction(NoInternetException()))
            }
        }

    fun signInButtonPressed() {
        signInGitHubAndStoreLoginData(loginFlow.value.toString(), authTokenFlow.value.toString())
    }

    fun validateLogin() = viewModelScope.launch {
        loginErrorStatus.tryEmit(Validations.validateLogin(loginFlow.value.toString()))
    }

    fun validateToken() = viewModelScope.launch {
        authTokenErrorStatus.tryEmit(Validations.validateAuthorisationToken(authTokenFlow.value.toString()))
    }

    sealed interface Action {
        data class ShowToastAction(val message: String) : Action
        data class ShowErrorAction(val exception: Throwable) : Action
        object RouteSuccessAction : Action
        object SetNormalStateAction : Action
        object SetLoadingStateAction : Action
    }
}