package app.thirtyninth.githubviewer.ui.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.network.Status
import app.thirtyninth.githubviewer.databinding.LoginFragmentBinding
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: LoginViewModel by viewModels()
    private val binding:LoginFragmentBinding by viewBinding(CreateMethod.INFLATE)
    private var isCorrectUserName = false
    private var isCorrectAccessToken = false
    private var isLoadingState = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            userLogin.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    CoroutineScope(IO).launch {
                        delay(150)

                        viewModel.validateUserName(p0.toString())
                    }
                }

            })

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

        viewModel.uiState.onEach {
            when (it) {
                UIState.NORMAL ->{
                    setNormalState()
                }
                UIState.LOADING ->{
                    setLoadingState()
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
                else -> {
                    //PLACEHOLDER
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
                else -> {
                    //PLACEHOLDER
                }
            }
        }.launchIn(lifecycleScope)

        lifecycleScope.launchWhenStarted {
            viewModel.user
                .onEach {
                    when (it.status) {
                        Status.SUCCESS -> {
                            navigateToRepositoryList()
                        }

                        Status.ERROR -> {
                            setNormalState()
                        }else ->{

                        }
                    }
                }.collect()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun navigateToRepositoryList(){
        val action = LoginFragmentDirections.navigateToRepositoryList()

        findNavController().navigate(action)
    }

    private fun setNormalState(){
        with(binding){
            signInButton.text = getString(R.string.sign_in)
            progressCircular.visibility = View.GONE
        }

        isLoadingState = false
    }

    private fun setLoadingState(){
        with(binding){
            signInButton.text = ""
            progressCircular.visibility = View.VISIBLE
        }

        isLoadingState = true
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
        if (!isLoadingState){
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
        binding.userLoginField.error = errorMessage
    }

    private fun setAccessTokenFieldError(errorMessage: String?) {
        binding.accessTokenField.error = errorMessage
    }
}