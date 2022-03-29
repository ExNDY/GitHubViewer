package app.thirtyninth.githubviewer.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.databinding.AuthFragmentBinding
import app.thirtyninth.githubviewer.extentions.bindTextTwoWayFlow
import app.thirtyninth.githubviewer.ui.main.viewmodel.AuthViewModel
import app.thirtyninth.githubviewer.ui.main.viewmodel.AuthViewModel.Action
import app.thirtyninth.githubviewer.ui.main.viewmodel.AuthViewModel.AuthScreenState
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
        with(binding) {
            authToken.bindTextTwoWayFlow(
                mutableStateFlow = viewModel.authTokenFlow,
                scope = lifecycleScope
            )

            signInButton.setOnClickListener {
                viewModel.onSignInButtonPressed()
            }
        }
    }

    private fun setupObservers() {
        viewModel.state.onEach { state ->
            handleState(state)
        }.launchIn(lifecycleScope)

        viewModel.actions.onEach { action ->
            handleAction(action)
        }.launchIn(lifecycleScope)

//        viewModel.authTokenValidation.onEach { status ->
//            setAuthTokenErrorMessage(status)
//        }
    }

    private fun routeToRepositoriesList() {
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

    private fun setAuthTokenErrorMessage(errorMessage: String) {
        with(binding) {
            accessTokenContainer.error = errorMessage
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.RouteToRepositoryList -> routeToRepositoriesList()
            is Action.ShowError -> {}
        }
    }

    private fun handleState(state: AuthScreenState) {
        when (state) {
            AuthScreenState.Idle -> setLoadingState()
            AuthScreenState.Loaded -> setNormalState()
            is AuthScreenState.InvalidAuthTokenInput -> setAuthTokenErrorMessage(
                state.reason.getString(
                    requireContext()
                )
            )
        }
    }
}