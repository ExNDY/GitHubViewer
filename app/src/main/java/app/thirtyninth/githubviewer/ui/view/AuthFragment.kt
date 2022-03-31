package app.thirtyninth.githubviewer.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.models.ExceptionBundle
import app.thirtyninth.githubviewer.databinding.AuthFragmentBinding
import app.thirtyninth.githubviewer.extentions.bindTextTwoWayFlow
import app.thirtyninth.githubviewer.ui.viewmodel.AuthViewModel
import app.thirtyninth.githubviewer.ui.viewmodel.AuthViewModel.Action
import app.thirtyninth.githubviewer.ui.viewmodel.AuthViewModel.ScreenState
import app.thirtyninth.githubviewer.utils.callExceptionDialog
import app.thirtyninth.githubviewer.utils.mapAlertMessage
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    handleAction(action)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    handleState(state)
                }
            }
        }
    }

    private fun routeToRepositoriesList() {
        findNavController().navigate(AuthFragmentDirections.routeToRepositoriesScreen())
    }

    private fun showErrorMessage(exceptionBundle: ExceptionBundle) {
        Timber.tag("EXCEPTION_BUNDLE_AUTH_SCREEN").d(exceptionBundle.toString())

        val title = exceptionBundle.title.getString(requireContext())
        val message = mapAlertMessage(exceptionBundle, requireContext())

        callExceptionDialog(title, message, requireContext()) {
            Timber.tag("EXCEPTION_AUTH_SCREEN").e(exceptionBundle.toString())
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.RouteToRepositoryList -> routeToRepositoriesList()
            is Action.ShowError -> showErrorMessage(action.exceptionBundle)
        }
    }

    private fun handleState(state: ScreenState) {
        with(binding) {
            accessTokenContainer.error = if (state is ScreenState.InvalidAuthTokenInput) {
                state.reason.getString(requireContext())
            } else {
                null
            }

            signInButton.text = if (state is ScreenState.Idle) {
                ""
            } else {
                getText(R.string.sign_in)
            }

            progressCircular.visibility = if (state is ScreenState.Idle) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}