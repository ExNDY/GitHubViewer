package app.thirtyninth.githubviewer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.thirtyninth.githubviewer.data.models.User
import app.thirtyninth.githubviewer.data.network.Resource
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.repository.Repository
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

    private val _user = MutableSharedFlow<Resource<User>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val user:SharedFlow<Resource<User>> = _user.asSharedFlow()

    private fun setUser(){
        _user.tryEmit(Resource.loading(null))
    }

    private fun getUser() = viewModelScope.launch {
        _user.tryEmit(Resource.loading(null))

        repository.getUser("token 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", "user").let {response ->
            if (response.isSuccessful){
                _user.tryEmit(Resource.success(response.body()))
            } else {
                _user.tryEmit(Resource.error(response.errorBody().toString(), null))
            }
        }
    }

    fun loadUser(){
        getUser()
    }
}