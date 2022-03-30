package app.thirtyninth.githubviewer.ui.main.view

import android.app.AlertDialog
import android.graphics.Color
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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.thirtyninth.githubviewer.AppNavigationDirections
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.common.Constants
import app.thirtyninth.githubviewer.data.models.ExceptionBundle
import app.thirtyninth.githubviewer.databinding.RepositoriesFragmentBinding
import app.thirtyninth.githubviewer.ui.adapters.RepositoryListAdapter
import app.thirtyninth.githubviewer.ui.interfaces.ActionListener
import app.thirtyninth.githubviewer.ui.main.viewmodel.RepositoriesViewModel
import app.thirtyninth.githubviewer.ui.main.viewmodel.RepositoriesViewModel.Action
import app.thirtyninth.githubviewer.ui.main.viewmodel.RepositoriesViewModel.RepositoriesListScreenState
import app.thirtyninth.githubviewer.utils.LanguageColorReader
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUIComponents()
        setupToolbar()
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
        val colors: Map<String, Color> = LanguageColorReader().fetchLanguageColorsMap(
            requireContext(), Constants.LANGUAGE_COLORS_JSON_PATH
        )

        val listAdapter = RepositoryListAdapter(colors, listener = this)

        setupObservers(listAdapter)

        //TODO Переделать click listener item
        with(binding) {
            repositoryList.apply {
                setHasFixedSize(true)
                addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                adapter = listAdapter
            }

            blockError.retryButton.setOnClickListener {
                viewModel.loadData()
            }
        }
    }

    private fun routeToAuthScreen() {
        findNavController().navigate(AppNavigationDirections.navigateToLoginScreen())
    }

    private fun openRepositoryDetail(owner: String, repositoryName: String) {
        findNavController().navigate(
            RepositoriesFragmentDirections.navigateToDetailInfo(
                owner, repositoryName
            )
        )
    }

    private fun setupObservers(adapter: RepositoryListAdapter) {
        // TODO разобраться почему не работает обычный Collect
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.onEach { action ->
                    handleAction(action)
                }.launchIn(lifecycleScope)
                viewModel.state.onEach { state ->
                    handleState(state, adapter)
                }.launchIn(lifecycleScope)
            }
        }
    }

    private fun handleEmptyState(exceptionBundle: ExceptionBundle) {
        val title = exceptionBundle.title.getString(requireContext())
        val message = exceptionBundle.message.getString(requireContext())
        val imageId = exceptionBundle.imageResId
        val titleColor = exceptionBundle.titleColor

        with(binding) {
            blockError.errorTitle.setTextColor(resources.getColor(titleColor, resources.newTheme()))
            blockError.errorTitle.text = title
            blockError.errorMessage.text = message
            blockError.errorImg.setImageResource(imageId)

            blockError.retryButton.setOnClickListener {
                viewModel.onRetryClicked()
            }
        }
    }

    private fun logout() = AlertDialog.Builder(context)
        .setTitle(getString(R.string.logout_dialog_title))
        .setMessage(getString(R.string.logout_dialog_message))
        .setPositiveButton(getString(R.string.logout_dialog_positive)) { _, _ ->
            viewModel.onLogoutClicked()
        }
        .setNegativeButton(getString(R.string.logout_dialog_negative), null)
        .show()

    override fun onClick(clickedPosition: Int) {
        //FIXME
        //openRepositoryDetail(item, item.repositoryName)
    }

    private fun handleState(state: RepositoriesListScreenState, adapter: RepositoryListAdapter) {
        with(binding) {
            blockError.container.visibility = if (state is RepositoriesListScreenState.Error) {
                handleEmptyState(state.exceptionBundle)
                View.VISIBLE
            } else {
                View.GONE
            }

            repositoryList.visibility = if (state is RepositoriesListScreenState.Loaded) {
                adapter.submitList(state.repos)
                View.VISIBLE
            } else {
                View.GONE
            }

            blockLoading.container.visibility = if (state is RepositoriesListScreenState.Loading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.RouteToAuthScreen -> routeToAuthScreen()
        }
    }
}