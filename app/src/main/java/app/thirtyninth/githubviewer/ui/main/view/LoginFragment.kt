package app.thirtyninth.githubviewer.ui.main.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private var isCorrectUserName = false
    private var isCorrectAccessToken = false
    private var isLoadingState = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            userLogin.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    viewModel.validateUserName(p0.toString())
                }
            })

            accessToken.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    viewModel.validateToken(p0.toString())
                }
            })

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
                UIState.SUCCESS -> {
                    navigateToRepositoryList()
                }
                else -> {

                }
            }
        }.launchIn(lifecycleScope)

        viewModel.userNameValid.onEach {
            isCorrectUserName = when (it) {
                UsernameState.CORRECT -> {
                    setUsernameFieldError(null)
                    true
                }
                UsernameState.INVALID -> {
                    setUsernameFieldError(getString(R.string.error_invalid_username))
                    false
                }
                UsernameState.EMPTY -> {
                    setUsernameFieldError(getString(R.string.error_username_is_empty))
                    false
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.authorisationTokenValid.onEach {
            isCorrectAccessToken = when (it) {
                TokenState.CORRECT -> {
                    setAccessTokenFieldError(null)
                    true
                }
                TokenState.INVALID -> {
                    setAccessTokenFieldError(getString(R.string.error_invalid_token))
                    false
                }
                TokenState.EMPTY -> {
                    setAccessTokenFieldError(getString(R.string.error_token_is_empty))
                    false
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            when (it) {
                NetworkExceptionType.UNAUTHORIZED -> {
                    setAccessTokenFieldError(getString(R.string.request_error_401_authentication_error))

                    lifecycleScope.run {
                        delay(2000)
                    }

                    setAccessTokenFieldError("")
                }
                NetworkExceptionType.NOT_MODIFIED -> {
                    showToast("Status: 304 Not Modified")
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

        isLoadingState = false
    }

    override fun setLoadingState() {
        with(binding) {
            signInButton.text = ""
            progressCircular.visibility = View.VISIBLE
        }

        isLoadingState = true
    }

    override fun setErrorState() {

    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    private fun signIn(username: String, token: String) {
        if (!isLoadingState) {
            if (!isCorrectUserName || !isCorrectAccessToken) {
                if (!isCorrectUserName) {
                    setUsernameFieldError(getString(R.string.error_username_is_empty))
                }

                if (!isCorrectAccessToken) {
                    setAccessTokenFieldError(getString(R.string.error_token_is_empty))
                }
            } else if (isCorrectUserName && isCorrectAccessToken) {
                viewModel.signInGitHubAndStoreLoginData(username, token)
            }
        }
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