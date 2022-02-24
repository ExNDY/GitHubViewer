package app.thirtyninth.githubviewer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.LoginData
import app.thirtyninth.githubviewer.data.network.Result
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.repository.Repository
import app.thirtyninth.githubviewer.utils.LoginUtils
import app.thirtyninth.githubviewer.utils.TokenState
import app.thirtyninth.githubviewer.utils.UIState
import app.thirtyninth.githubviewer.utils.UsernameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState.NORMAL)
    val uiState: StateFlow<UIState> get() = _uiState.asStateFlow()

    private val _errorFlow = MutableStateFlow(-13)
    val errorFlow: StateFlow<Int> get() = _errorFlow.asStateFlow()

    private val _userNameValid = MutableStateFlow(UsernameState.CORRECT)
    val userNameValid: StateFlow<UsernameState> get() = _userNameValid

    private val _authorisationTokenValid = MutableStateFlow(TokenState.CORRECT)
    val authorisationTokenValid: StateFlow<TokenState> get() = _authorisationTokenValid


    fun signInGitHubAndStoreLoginData(username: String, token: String) = viewModelScope.launch {
        _uiState.tryEmit(UIState.LOADING)

        when (val result = repository.getUser("token $token")) {
            is Result.Success -> {
                val user = result.data

                if (user != null) {
                    if (user.login.equals(username, true)) {
                        userPreferences.saveUser(LoginData(username, token))

                        _uiState.tryEmit(UIState.SUCCESS)
                    } else {
                        _uiState.tryEmit(UIState.NORMAL)
                        _userNameValid.tryEmit(UsernameState.INVALID)
                    }
                } else {
                    _uiState.tryEmit(UIState.NORMAL)
                    _errorFlow.tryEmit(-1)
                }
            }
            is Result.Error -> {
                val errorCode:Int = result.code ?: 0

                _errorFlow.tryEmit(errorCode)
                _uiState.tryEmit(UIState.NORMAL)
            }
        }
    }

    fun validateUserName(userName: String) = viewModelScope.launch {
        _userNameValid.value = LoginUtils.validateUserName(userName)
    }

    fun validateToken(token: String) = viewModelScope.launch {
        _authorisationTokenValid.value = LoginUtils.validateAuthorisationToken(token)
    }
}