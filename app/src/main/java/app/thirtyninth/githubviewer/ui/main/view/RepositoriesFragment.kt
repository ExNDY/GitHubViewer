package app.thirtyninth.githubviewer.ui.main.view

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.thirtyninth.githubviewer.AppNavigationDirections
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.common.Constants
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.databinding.RepositoriesFragmentBinding
import app.thirtyninth.githubviewer.ui.adapters.RepositoryListAdapter
import app.thirtyninth.githubviewer.ui.interfaces.ActionListener
import app.thirtyninth.githubviewer.ui.main.viewmodel.RepositoriesViewModel
import app.thirtyninth.githubviewer.ui.main.viewmodel.RepositoriesViewModel.Action
import app.thirtyninth.githubviewer.ui.main.viewmodel.RepositoriesViewModel.RepositoriesListScreenState
import app.thirtyninth.githubviewer.utils.StorageUtil
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class RepositoriesFragment : Fragment(), ActionListener {
    private val viewModel: RepositoriesViewModel by viewModels()
    private val binding: RepositoriesFragmentBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        initApp()

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUIComponents()
        setupToolbar()
    }

    private fun initApp() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isLoggedIn
                .onEach { isLoggedIn ->
                    if (!isLoggedIn) {
                        routeToAuthScreen()
                    }
                }.collect()
        }
    }

    private fun setupToolbar() {
        with(binding) {
            toolbar.inflateMenu(R.menu.action_bar_menu)

            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.logout -> {
                        logout()
                    }
                }

                true
            }

            toolbar.title = getString(R.string.fragment_name_repositories_list)
        }
    }

    private fun setupUIComponents() {
        val colors: Map<String, Color> = StorageUtil.fetchLanguageColorsMap(
            requireContext(), Constants.LANGUAGE_COLORS_JSON_PATH
        )

        val listAdapter = RepositoryListAdapter(colors, listener = this)

        setupObservers(listAdapter)

        with(binding) {
            rvRepositoryList.apply {
                setHasFixedSize(true)
                addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                adapter = listAdapter
            }

            errorBlock.retryButton.setOnClickListener {
                viewModel.loadData()
            }
        }
    }

    private fun routeToAuthScreen() {
        findNavController().navigate(AppNavigationDirections.navigateToLoginScreen())
    }

    private fun openRepositoryDetail(owner: String, repositoryName: String) {
        findNavController().navigate(
            RepositoriesFragmentDirections.navigateToRepositoryInfo(
                owner, repositoryName
            )
        )
    }

    private fun setupObservers(adapter: RepositoryListAdapter) {
        viewModel.actions.onEach { action ->
            handleAction(action)
        }.launchIn(lifecycleScope)

        viewModel.state.onEach { state ->
            handleState(state, adapter)
        }.launchIn(lifecycleScope)
    }

    private fun setLoadingState() {
        with(binding) {
            errorBlock.errorContainer.visibility = View.GONE
            progressHorizontal.visibility = View.VISIBLE
            rvRepositoryList.visibility = View.VISIBLE
        }
    }

    private fun setLoadedState(list: List<GitHubRepository>, adapter: RepositoryListAdapter) {
        with(binding) {
            errorBlock.errorContainer.visibility = View.GONE
            progressHorizontal.visibility = View.GONE
        }

        adapter.submitList(list)
    }

    private fun setErrorState(title:String, message: String) {
        with(binding) {
            progressHorizontal.visibility = View.GONE
            rvRepositoryList.visibility = View.GONE
            errorBlock.errorContainer.visibility = View.VISIBLE

            errorBlock.errorMessage.text = message
        }
    }

    private fun setEmptyState() {
        //TODO
    }

    private fun logout() = AlertDialog.Builder(context)
        .setTitle(getString(R.string.logout_dialog_title))
        .setMessage(getString(R.string.logout_dialog_message))
        .setPositiveButton(getString(R.string.logout_dialog_positive)) { _, _ ->
            viewModel.onLogoutClicked()
        }
        .setNegativeButton(getString(R.string.logout_dialog_negative), null)
        .show()

    override fun onClick(clickedPosition: Int, owner: String, repositoryName: String) {
        openRepositoryDetail(owner, repositoryName)
    }

    private fun handleState(state: RepositoriesListScreenState, adapter: RepositoryListAdapter) {
        when (state) {
            RepositoriesListScreenState.Loading -> setLoadingState()
            is RepositoriesListScreenState.Loaded -> setLoadedState(state.repos, adapter)
            is RepositoriesListScreenState.Error -> setErrorState("Title", state.error.getString(requireContext()))
            is RepositoriesListScreenState.Empty -> setEmptyState()
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.RouteToAuthScreen -> routeToAuthScreen()
        }
    }
}