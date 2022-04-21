package app.thirtyninth.githubviewer.ui.view

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
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.databinding.RepositoriesFragmentBinding
import app.thirtyninth.githubviewer.ui.adapters.RepositoryListAdapter
import app.thirtyninth.githubviewer.ui.viewmodel.RepositoriesViewModel
import app.thirtyninth.githubviewer.ui.viewmodel.RepositoriesViewModel.Action
import app.thirtyninth.githubviewer.ui.viewmodel.RepositoriesViewModel.ScreenState
import app.thirtyninth.githubviewer.utils.LanguageColorReader
import app.thirtyninth.githubviewer.utils.callLogoutDialog
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RepositoriesFragment : Fragment() {
    private val TAG = RepositoriesFragment::class.java.simpleName

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

        setupRepositoriesList()
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

    private fun setupRepositoriesList() {
        val colors: Map<String, Color> = LanguageColorReader().fetchLanguageColorsMap(
            requireContext(), Constants.LANGUAGE_COLORS_JSON_PATH
        )

        val repositoriesListAdapter = RepositoryListAdapter(colors) { clickedPosition ->
            openRepositoryDetail(clickedPosition)
        }

        setupObservers(repositoriesListAdapter)

        with(binding) {
            repositoryList.apply {
                setHasFixedSize(true)
                addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                adapter = repositoriesListAdapter
            }

            blockError.retryButton.setOnClickListener {
                viewModel.loadData()
            }
        }
    }

    private fun openRepositoryDetail(position: Int) {
        val item: GitHubRepository =
            (binding.repositoryList.adapter as RepositoryListAdapter).getItem(position)

        val owner = item.owner!!.login
        val repositoryName = item.name!!

        findNavController().navigate(
            RepositoriesFragmentDirections.navigateToDetailInfo(
                owner, repositoryName
            )
        )
    }

    private fun routeToAuthScreen() {
        findNavController().navigate(AppNavigationDirections.routeToAuthScreen())
    }

    private fun setupObservers(adapter: RepositoryListAdapter) {
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
                    handleState(state, adapter)
                }
            }
        }
    }

    private fun logout() = callLogoutDialog(requireContext()) { viewModel.onLogoutClicked() }

    private fun handleErrorState(state: ScreenState) {

        val title = if (state is ScreenState.Error) {
            state.exceptionBundle.title.getString(requireContext())
        } else {
            ""
        }

        val message = if (state is ScreenState.Error) {
            state.exceptionBundle.message.getString(requireContext())
        } else {
            ""
        }

        val imageId = if (state is ScreenState.Error) {
            state.exceptionBundle.imageResId
        } else {
            null
        }
        val colorId = if (state is ScreenState.Error) {
            state.exceptionBundle.colorResId
        } else {
            null
        }

        with(binding) {
            if (colorId != null) {
                blockError.errorTitle.setTextColor(
                    resources.getColor(
                        colorId,
                        resources.newTheme()
                    )
                )
            }

            if (imageId != null) {
                blockError.errorImg.visibility = View.VISIBLE

                blockError.errorImg.setImageResource(imageId)
            } else {
                blockError.errorImg.visibility = View.GONE
            }

            blockError.errorTitle.text = title
            blockError.errorMessage.text = message

            if (state is ScreenState.Error) {
                blockError.retryButton.setOnClickListener {
                    viewModel.onRetryClicked()
                }
            }
        }
    }

    private fun handleState(state: ScreenState, adapter: RepositoryListAdapter) {
        Timber.tag(TAG).d(state.toString())

        with(binding) {
            blockError.container.visibility =
                if (state is ScreenState.Error) View.VISIBLE else View.GONE

            repositoryList.visibility = if (state is ScreenState.Loaded) View.VISIBLE else View.GONE

            blockLoading.container.visibility =
                if (state is ScreenState.Loading) View.VISIBLE else View.GONE
        }

        if (state is ScreenState.Error) {
            handleErrorState(state)
        }

        if (state is ScreenState.Loaded) {
            adapter.submitList(state.repos)
        } else {
            adapter.submitList(emptyList())
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            Action.RouteToAuthScreen -> routeToAuthScreen()
        }
    }
}