package app.thirtyninth.githubviewer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.LoginData
import app.thirtyninth.githubviewer.data.models.User
import app.thirtyninth.githubviewer.data.network.Resource
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.repository.Repository
import app.thirtyninth.githubviewer.utils.LoginUtils
import app.thirtyninth.githubviewer.utils.TokenState
import app.thirtyninth.githubviewer.utils.UIState
import app.thirtyninth.githubviewer.utils.UsernameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _user = MutableSharedFlow<Resource<User?>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val user: SharedFlow<Resource<User?>> = _user.asSharedFlow()

    private val _uiState = MutableStateFlow(UIState.NORMAL)
    val uiState:StateFlow<UIState> get() = _uiState

    private val _userNameValid = MutableStateFlow(UsernameState.CORRECT)
    val userNameValid: StateFlow<UsernameState> get() = _userNameValid

    private val _authorisationTokenValid = MutableStateFlow(TokenState.CORRECT)
    val authorisationTokenValid: StateFlow<TokenState> get() = _authorisationTokenValid


    fun signInGitHubAndStoreLoginData(username: String, token: String) = viewModelScope.launch{
        _uiState.tryEmit(UIState.LOADING)

        var userData:User? = null

        try {
            userData = repository.getUser("token $token")
        } catch (ex: Exception) {
            _user.tryEmit(Resource.error(ex.toString(), null))
        }

        if (userData != null) {
            if (userData.login.equals(username, true)){
                userPreferences.saveUser(LoginData(username, token))

                _uiState.tryEmit(UIState.NORMAL)
                _user.tryEmit(Resource.success(userData))
            } else {
                _uiState.tryEmit(UIState.NORMAL)
                _userNameValid.tryEmit(UsernameState.INVALID)
            }
        }
    }

    fun validateUserName(userName: String) = viewModelScope.launch{
        _userNameValid.value = LoginUtils.validateUserName(userName)
    }

    fun validateToken(token:String) = viewModelScope.launch {
        _authorisationTokenValid.value = LoginUtils.validateAuthorisationToken(token)
    }
}