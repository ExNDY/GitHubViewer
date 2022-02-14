package app.thirtyninth.githubviewer.ui.viewmodel

import androidx.lifecycle.ViewModel
import app.thirtyninth.githubviewer.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val repository: Repository): ViewModel() {
    // TODO: Implement the ViewModel
}