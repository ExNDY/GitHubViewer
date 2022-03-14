package app.thirtyninth.githubviewer.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.thirtyninth.githubviewer.AppNavigationDirections
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.network.NetworkExceptionType
import app.thirtyninth.githubviewer.databinding.RepositoriesFragmentBinding
import app.thirtyninth.githubviewer.ui.adapters.RepositoryListAdapter
import app.thirtyninth.githubviewer.ui.base.BaseFragment
import app.thirtyninth.githubviewer.ui.main.viewmodel.RepositoriesViewModel
import app.thirtyninth.githubviewer.utils.StorageUtil
import app.thirtyninth.githubviewer.utils.UIState
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.JsonObject

@AndroidEntryPoint
class RepositoriesFragment : BaseFragment() {
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
                .onEach {
                    if (it) {
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

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
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
        val colors: JsonObject = requireContext().let {
            StorageUtil.jsonToLanguageColorList(it, "LanguageColors.json")
        }

        val listAdapter = RepositoryListAdapter(
            colors
        ) {
            findNavController().navigate(
                RepositoriesFragmentDirections.navigateToRepositoryInfo(
                    it.owner?.login.toString(),
                    it.name.toString()
                )
            )
        }

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

    private fun setupObservers() {
        viewModel.repositoryList
            .onEach {
                (binding.rvRepositoryList.adapter as RepositoryListAdapter).submitList(
                    it
                )
            }.launchIn(lifecycleScope)

        viewModel.uiState.onEach {
            when (it) {
                UIState.NORMAL -> {
                    setNormalState()
                }
                UIState.LOADING -> {
                    setLoadingState()
                }
                UIState.ERROR -> {
                    setErrorState()
                }
                else -> {

                }
            }
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            when (it) {
                NetworkExceptionType.SERVER_ERROR -> {
                    setErrorMessage(getString(R.string.request_error_connection_with_server))
                }

                NetworkExceptionType.UNAUTHORIZED -> {
                    setErrorMessage(getString(R.string.request_error_401_authentication_error))
                }

                else -> {
                    viewModel.errorMessage.onEach { msg ->
                        if (msg.isNotEmpty()) {
                            setErrorMessage(msg)
                        } else {
                            setErrorMessage("")
                        }
                    }
                }
            }
        }.launchIn(lifecycleScope)
    }

    override fun setNormalState() {
        with(binding) {
            errorBlock.visibility = View.GONE
            progressHorizontal.visibility = View.GONE
        }
    }

    override fun setLoadingState() {
        with(binding) {
            errorBlock.visibility = View.GONE
            progressHorizontal.visibility = View.VISIBLE
            rvRepositoryList.visibility = View.VISIBLE
        }
    }

    override fun setErrorState() {
        with(binding) {
            progressHorizontal.visibility = View.GONE
            rvRepositoryList.visibility = View.GONE
            errorBlock.visibility = View.VISIBLE
        }
    }

    private fun setErrorMessage(message: String) {
        with(binding) {
            errorMessage.text = message
        }
    }
}