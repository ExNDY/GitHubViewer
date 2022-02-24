package app.thirtyninth.githubviewer.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.thirtyninth.githubviewer.AppNavigationDirections
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.databinding.RepositoriesFragmentBinding
import app.thirtyninth.githubviewer.ui.adapters.RepositoryListAdapter
import app.thirtyninth.githubviewer.ui.view.base.BaseFragment
import app.thirtyninth.githubviewer.ui.viewmodel.RepositoriesViewModel
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

    private lateinit var listAdapter: RepositoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initApp()

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUIComponents()
    }

    private fun initApp() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isLoggedIn
                .onEach {
                    if (it) {
                        setupObservers()
                    } else {
                        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
                        findNavController().navigate(AppNavigationDirections.navigateToLoginScreen())
                    }
                }.collect()
        }
    }

    private fun setupUIComponents() {
        val colors: JsonObject

        lifecycleScope.run {
            colors =
                context?.let { StorageUtil.jsonToLanguageColorList(it, "LanguageColors.json") }!!
        }

        listAdapter = RepositoryListAdapter(
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
            when(it){
                -13->{

                }
                -1 -> {
                    setErrorMessage(getString(R.string.request_error_connection_with_server))
                }
                (401) ->{
                    setErrorMessage(getString(R.string.request_error_401_authentication_error))
                } else ->{
                    viewModel.errorMessage.onEach {msg ->
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                viewModel.logout()
                findNavController().navigate(AppNavigationDirections.navigateToLoginScreen())
                return true
            }
        }

        return false
    }

}