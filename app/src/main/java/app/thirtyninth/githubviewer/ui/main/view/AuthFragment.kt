package app.thirtyninth.githubviewer.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.databinding.AuthFragmentBinding
import app.thirtyninth.githubviewer.extentions.bindTextTwoWayFlow
import app.thirtyninth.githubviewer.ui.interfaces.Validation
import app.thirtyninth.githubviewer.ui.main.viewmodel.AuthViewModel
import app.thirtyninth.githubviewer.ui.main.viewmodel.AuthViewModel.Action
import app.thirtyninth.githubviewer.utils.mapValidation
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class AuthFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private val binding: AuthFragmentBinding by viewBinding(CreateMethod.INFLATE)

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
            userLogin.bindTextTwoWayFlow(
                stateFlow = viewModel.loginFlow,
                lifecycleScope = lifecycleScope,
                afterTextChanged = { viewModel.validateLogin() }
            )
            authToken.bindTextTwoWayFlow(
                stateFlow = viewModel.authTokenFlow,
                lifecycleScope = lifecycleScope,
                afterTextChanged = { viewModel.validateToken() }
            )

            signInButton.setOnClickListener {
                viewModel.signInButtonPressed()
            }
        }
    }

    private fun setupObservers() {
        viewModel.authTokenErrorStatus.onEach { status ->
            setAuthTokenErrorMessage(status)
        }.launchIn(lifecycleScope)

        viewModel.loginErrorStatus.onEach { status ->
            setLoginErrorMessage(status)
        }.launchIn(lifecycleScope)
    }

    private fun navigateToRepositoryList() {
        findNavController().navigate(AuthFragmentDirections.navigateToRepositoryList())
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

    private fun setLoginErrorMessage(status: Validation) {
        val message = mapValidation(status, requireContext().resources)

        with(binding) {
            userLoginContainer.error = message
        }
    }

    private fun setAuthTokenErrorMessage(status: Validation) {
        val message = mapValidation(status, requireContext().resources)

        with(binding) {
            accessTokenContainer.error = message
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.RouteSuccessAction -> {
                setNormalState()
                navigateToRepositoryList()
            }
            is Action.ShowToastAction -> showToast(action.message)
            is Action.ShowErrorAction -> {}
            Action.SetNormalStateAction -> setNormalState()
            Action.SetLoadingStateAction -> setLoadingState()
        }
    }
}