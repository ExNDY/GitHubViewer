package app.thirtyninth.githubviewer.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.network.NetworkExceptionType
import app.thirtyninth.githubviewer.databinding.LoginFragmentBinding
import app.thirtyninth.githubviewer.ui.base.BaseFragment
import app.thirtyninth.githubviewer.ui.main.viewmodel.LoginViewModel
import app.thirtyninth.githubviewer.utils.TokenState
import app.thirtyninth.githubviewer.utils.UIState
import app.thirtyninth.githubviewer.utils.UsernameState
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class LoginFragment : BaseFragment() {
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
        with(binding) {
            userLogin.doAfterTextChanged {
                viewModel.validateUserName(it.toString())
            }

            accessToken.doAfterTextChanged {
                viewModel.validateToken(it.toString())
            }

            signInButton.setOnClickListener {
                signIn(binding.userLogin.text.toString(), binding.accessToken.text.toString())
            }
        }
    }

    private fun setupObservers() {
        viewModel.uiState.onEach {
            when (it) {
                UIState.NORMAL -> {
                    setNormalState()
                }
                UIState.LOADING -> {
                    setLoadingState()
                }
                else -> {

                }
            }
        }.launchIn(lifecycleScope)

        viewModel.userNameValid.onEach {
            when (it) {
                UsernameState.CORRECT -> {
                    setUsernameFieldError(null)
                }
                UsernameState.INVALID -> {
                    setUsernameFieldError(getString(R.string.error_invalid_username))
                }
                UsernameState.EMPTY -> {
                    setUsernameFieldError(getString(R.string.error_username_is_empty))
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.authorisationTokenValid.onEach {
            when (it) {
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

        viewModel.isLoggedInSuccess.onEach {
            if (it){
                navigateToRepositoryList()
            }
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            setNormalState()

            when (it) {
                NetworkExceptionType.UNAUTHORIZED -> {
                    setAccessTokenFieldError(getString(R.string.request_error_401_authentication_error))

                    delay(2000)

                    setAccessTokenFieldError("")
                }

                NetworkExceptionType.SERVER_ERROR -> {
                    showToast(getString(R.string.request_error_connection_with_server))
                }
                else -> {
                    showToast("Something wrong. Please try later")
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun navigateToRepositoryList() {
        findNavController().navigate(LoginFragmentDirections.navigateToRepositoryList())
    }

    override fun setNormalState() {
        with(binding) {
            signInButton.text = getString(R.string.sign_in)
            progressCircular.visibility = View.GONE
        }
    }

    override fun setLoadingState() {
        with(binding) {
            signInButton.text = ""
            progressCircular.visibility = View.VISIBLE
        }
    }

    override fun setErrorState() {

    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun signIn(username: String, token: String) {
        viewModel.signInGitHubAndStoreLoginData(username, token)
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
}