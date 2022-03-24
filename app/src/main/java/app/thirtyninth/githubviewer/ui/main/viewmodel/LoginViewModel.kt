package app.thirtyninth.githubviewer.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.LoginData
import app.thirtyninth.githubviewer.data.network.NetworkExceptionType
import app.thirtyninth.githubviewer.data.network.NoInternetException
import app.thirtyninth.githubviewer.data.network.NotFoundException
import app.thirtyninth.githubviewer.data.network.Result
import app.thirtyninth.githubviewer.data.network.UnauthorizedException
import app.thirtyninth.githubviewer.data.network.UnexpectedException
import app.thirtyninth.githubviewer.data.repository.GitHubViewerRepository
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.utils.TokenState
import app.thirtyninth.githubviewer.utils.UIState
import app.thirtyninth.githubviewer.utils.UsernameState
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
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: GitHubViewerRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState.NORMAL)
    val uiState: StateFlow<UIState> get() = _uiState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<NetworkExceptionType>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val errorFlow: SharedFlow<NetworkExceptionType> = _errorFlow.asSharedFlow()

    private val _userNameValid = MutableStateFlow(UsernameState.CORRECT)
    val userNameValid: StateFlow<UsernameState> get() = _userNameValid

    private val _authorisationTokenValid = MutableStateFlow(TokenState.CORRECT)
    val authorisationTokenValid: StateFlow<TokenState> get() = _authorisationTokenValid

    private val _isLoggedInSuccess = MutableStateFlow(false)
    val isLoggedInSuccess: StateFlow<Boolean> get() = _isLoggedInSuccess

    fun signInGitHubAndStoreLoginData(username: String, token: String) = viewModelScope.launch {
        if (Variables.isNetworkConnected) {
            _uiState.tryEmit(UIState.LOADING)

            when (val result = repository.getUserInfo("token $token")) {
                is Result.Success -> {
                    val user = result.data

                    if (user != null) {
                        if (user.login.equals(username, true)) {
                            userPreferences.saveUser(LoginData(username, token))

                            _isLoggedInSuccess.tryEmit(true)
                        } else {
                            _uiState.tryEmit(UIState.NORMAL)
                            _userNameValid.tryEmit(UsernameState.INVALID)
                        }
                    } else {
                        _uiState.tryEmit(UIState.NORMAL)
                        _errorFlow.tryEmit(NetworkExceptionType.EMPTY_DATA)
                    }
                }
                is Result.Error -> {
                    _uiState.tryEmit(UIState.ERROR)

                    when (result.exception) {
                        is NoInternetException -> {
                            _errorFlow.tryEmit(NetworkExceptionType.SERVER_ERROR)
                        }

                        is UnexpectedException -> {
                            _errorFlow.tryEmit(NetworkExceptionType.NOT_DETERMINED)
                        }

                        is UnauthorizedException -> {
                            _errorFlow.tryEmit(NetworkExceptionType.UNAUTHORIZED)
                        }

                        is NotFoundException -> {
                            //TODO ADD STATE FOR EXCEPTION
                        }

                        is HttpException -> {
                            _errorFlow.tryEmit(NetworkExceptionType.NOT_DETERMINED)
                        }
                    }

                }
            }
        } else {
            _uiState.tryEmit(UIState.NORMAL)
            _errorFlow.tryEmit(NetworkExceptionType.SERVER_ERROR)
        }
    }

    fun validateUserName(userName: String) = viewModelScope.launch {
        _userNameValid.value = Validations.validateUserName(userName)
    }

    fun validateToken(token: String) = viewModelScope.launch {
        _authorisationTokenValid.value = Validations.validateAuthorisationToken(token)
    }
}