package app.thirtyninth.githubviewer.ui.view

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
import app.thirtyninth.githubviewer.databinding.LoginFragmentBinding
import app.thirtyninth.githubviewer.ui.view.base.BaseFragment
import app.thirtyninth.githubviewer.ui.viewmodel.LoginViewModel
import app.thirtyninth.githubviewer.utils.TokenState
import app.thirtyninth.githubviewer.utils.UIState
import app.thirtyninth.githubviewer.utils.UsernameState
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private val viewModel: LoginViewModel by viewModels()
    private val binding: LoginFragmentBinding by viewBinding(CreateMethod.INFLATE)

    private var isCorrectUserName = false
    private var isCorrectAccessToken = false
    private var isLoadingState = false

    // FIXME упущен вызов super метода, из-за этого не будет работать системная логика восстановления
    //  instance state у вьюх
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
            // FIXME в отдельную функцию стоит вынести накидывание такого textWatcher'а
            userLogin.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    CoroutineScope(IO).launch {
                        // FIXME что дает этот delay?
                        delay(150)

                        viewModel.validateUserName(p0.toString())
                    }
                }

            })

            // FIXME в отдельную функцию стоит вынести накидывание такого textWatcher'а
            accessToken.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    CoroutineScope(IO).launch {
                        delay(250)

                        viewModel.validateToken(p0.toString())
                    }
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
            when (it) {
                UsernameState.CORRECT -> {
                    setUsernameFieldError(null)
                    isCorrectUserName = true
                }
                UsernameState.INVALID -> {
                    setUsernameFieldError(getString(R.string.error_invalid_username))
                    isCorrectUserName = false
                }
                UsernameState.EMPTY -> {
                    setUsernameFieldError(getString(R.string.error_username_is_empty))
                    isCorrectUserName = false
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.authorisationTokenValid.onEach {
            when (it) {
                TokenState.CORRECT -> {
                    setAccessTokenFieldError(null)
                    isCorrectAccessToken = true
                }
                TokenState.INVALID -> {
                    setAccessTokenFieldError(getString(R.string.error_invalid_token))
                    isCorrectAccessToken = false
                }
                TokenState.EMPTY -> {
                    setAccessTokenFieldError(getString(R.string.error_token_is_empty))
                    isCorrectAccessToken = false
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            // FIXME ненадежная реализация - константы хардкодные без имен, если они поменяются в
            //  одном месте то в другом могут быть забыты, плюс нет гарантии что в else это реально
            //  сервер недоступен будет. Я наткнулся на ошибку сериализации, а писало мне
            //  "сервер недоступен" что не правда
            when (it) {
                (-13)->{

                }
                (401) -> {
                    setAccessTokenFieldError(getString(R.string.request_error_401_authentication_error))

                    lifecycleScope.run {
                        delay(2000)
                    }

                    setAccessTokenFieldError("")
                }
                else -> {
                    showToast(getString(R.string.request_error_connection_with_server))
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

        // FIXME когда раздельно флаги выставляются - падает надежность реализации.
        //  будет сильно надежнее код если будет в одном месте в ui написано
        //  isLoadingState = <тут условие на основе данных от вьюмодели>
        isLoadingState = false
    }

    override fun setLoadingState() {
        with(binding) {
            signInButton.text = ""
            progressCircular.visibility = View.VISIBLE
        }

        isLoadingState = true
    }

    // FIXME зачем тогда состояние ошибки нам тут?
    override fun setErrorState() {

    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        // FIXME лучше скрой вообще на уровне темы его и используй Toolbar везде в своей верстке
        //  - больше контроля и меньше проблем с ним
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    private fun signIn(username: String, token: String) {
        // FIXME тут логика прям находится, на ui. а не должно быть - это все должна вьюмодель делать
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