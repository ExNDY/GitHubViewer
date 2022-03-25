package app.thirtyninth.githubviewer.ui.main.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.thirtyninth.githubviewer.AppNavigationDirections
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.common.Constants
import app.thirtyninth.githubviewer.databinding.RepositoriesFragmentBinding
import app.thirtyninth.githubviewer.ui.adapters.RepositoryListAdapter
import app.thirtyninth.githubviewer.ui.interfaces.ActionListener
import app.thirtyninth.githubviewer.ui.main.viewmodel.RepositoriesViewModel
import app.thirtyninth.githubviewer.ui.main.viewmodel.RepositoriesViewModel.Action
import app.thirtyninth.githubviewer.utils.StorageUtil
import app.thirtyninth.githubviewer.utils.mapExceptionToMessage
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
                    if (isLoggedIn) {
                        setupObservers()
                    } else {
                        findNavController().navigate(AppNavigationDirections.navigateToLoginScreen())
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
                        viewModel.logout()
                        findNavController().navigate(AppNavigationDirections.navigateToLoginScreen())
                    }
                    else -> {

                    }
                }

                true
            }

            toolbar.title = getString(R.string.fragment_name_repositories_list)
        }
    }

    private fun setupUIComponents() {
        val colors: Map<String, Color> = requireContext().let { context ->
            StorageUtil.fetchLanguageColorsMap(
                context, Constants.LANGUAGE_COLORS_JSON_PATH
            )
        }

        val listAdapter = RepositoryListAdapter(colors, listener = this)

        with(binding) {
            rvRepositoryList.apply {
                setHasFixedSize(true)
                addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                adapter = listAdapter
            }

            tryButton.setOnClickListener {
                viewModel.loadData()
            }
        }
    }

    private fun openRepositoryDetail(owner: String, repositoryName: String) {
        findNavController().navigate(
            RepositoriesFragmentDirections.navigateToRepositoryInfo(
                owner, repositoryName
            )
        )
    }

    private fun setupObservers() {
        viewModel.actions.onEach { action ->
            handleAction(action)
        }.launchIn(lifecycleScope)

        viewModel.repositoryList
            .onEach { repositoryList ->
                (binding.rvRepositoryList.adapter as RepositoryListAdapter)
                    .submitList(repositoryList)
            }.launchIn(lifecycleScope)
    }

    private fun setNormalState() {
        with(binding) {
            errorBlock.visibility = View.GONE
            progressHorizontal.visibility = View.GONE
        }
    }

    private fun setLoadingState() {
        with(binding) {
            errorBlock.visibility = View.GONE
            progressHorizontal.visibility = View.VISIBLE
            rvRepositoryList.visibility = View.VISIBLE
        }
    }

    private fun setErrorState(throwable: Throwable) {
        val message = mapExceptionToMessage(throwable, requireContext().resources)

        with(binding) {
            errorBlock.visibility = View.VISIBLE
            progressHorizontal.visibility = View.GONE
            rvRepositoryList.visibility = View.GONE
            errorMessage.text = message
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(clickedPosition: Int, owner: String, repositoryName: String) {
        openRepositoryDetail(owner, repositoryName)
    }

    private fun handleAction(action: Action) {
        when (action) {
            is Action.ShowToastAction -> showToast(action.message)
            is Action.ShowErrorAction -> setErrorState(action.exception)
            Action.SetNormalStateAction -> setNormalState()
            Action.SetLoadingStateAction -> setLoadingState()
        }
    }
}