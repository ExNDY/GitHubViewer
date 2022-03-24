package app.thirtyninth.githubviewer.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.databinding.LoginFragmentBinding
import app.thirtyninth.githubviewer.ui.main.viewmodel.LoginViewModel
import app.thirtyninth.githubviewer.ui.main.viewmodel.LoginViewModel.Action
import app.thirtyninth.githubviewer.utils.LoginState
import app.thirtyninth.githubviewer.utils.TokenState
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private val binding: LoginFragmentBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        setHasOptionsMenu(false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        viewModel.actions.onEach { action ->
            handleAction(action)
        }.launchIn(lifecycleScope)

        with(binding) {
            userLogin.doAfterTextChanged {
                viewModel.validateLogin(it.toString())
            }

            authToken.doAfterTextChanged {
                viewModel.validateToken(it.toString())
            }

            signInButton.setOnClickListener {
                signIn(
                    login = binding.userLogin.text.toString(),
                    authToken = binding.authToken.text.toString()
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.loginValid.onEach { state ->
            when (state) {
                LoginState.CORRECT -> {
                    setUsernameFieldError(null)
                }
                LoginState.INVALID -> {
                    setUsernameFieldError(getString(R.string.error_invalid_username))
                }
                LoginState.EMPTY -> {
                    setUsernameFieldError(getString(R.string.error_username_is_empty))
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.authorisationTokenValid.onEach { state ->
            when (state) {
                TokenState.CORRECT -> {
                    setAccessTokenFieldError(null)
                }
                TokenState.INVALID -> {
                    setAccessTokenFieldError(getString(R.string.error_invalid_token))
                }
                TokenState.EMPTY -> {
                    setAccessTokenFieldError(getString(R.string.error_token_is_empty))
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun navigateToRepositoryList() {
        findNavController().navigate(LoginFragmentDirections.navigateToRepositoryList())
    }

    private fun setNormalState() {
        with(binding) {
            signInButton.text = getString(R.string.sign_in)
            progressCircular.visibility = View.GONE
        }
    }

    private fun setLoadingState() {
        with(binding) {
            signInButton.text = ""
            progressCircular.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun signIn(login: String, authToken: String) {
        viewModel.signInGitHubAndStoreLoginData(login, authToken)
    }

    private fun setUsernameFieldError(errorMessage: String?) {
        with(binding) {
            userLoginContainer.error = errorMessage
        }
    }

    private fun setAccessTokenFieldError(errorMessage: String?) {
        with(binding) {
            accessTokenContainer.error = errorMessage
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.RouteSuccessAction -> {
                setNormalState()
                navigateToRepositoryList()
            }
            is Action.SignInAction -> signIn(
                login = action.login,
                authToken = action.authToken
            )
            is Action.ShowToastAction -> showToast(action.message)
            Action.SetNormalStateAction -> setNormalState()
            Action.SetLoadingStateAction -> setLoadingState()
            else -> {

            }
        }
    }
}