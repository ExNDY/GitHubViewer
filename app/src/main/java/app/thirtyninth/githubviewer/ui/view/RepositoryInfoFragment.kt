package app.thirtyninth.githubviewer.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.thirtyninth.githubviewer.AppNavigationDirections
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.databinding.RepositoryInfoFragmentBinding
import app.thirtyninth.githubviewer.ui.view.base.BaseFragment
import app.thirtyninth.githubviewer.ui.viewmodel.RepositoryInfoViewModel
import app.thirtyninth.githubviewer.utils.UIState
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class RepositoryInfoFragment : BaseFragment() {
    private val viewModel: RepositoryInfoViewModel by viewModels()
    private val binding: RepositoryInfoFragmentBinding by viewBinding(CreateMethod.INFLATE)
    private val args:RepositoryInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = args.repositoryName
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                viewModel.logout()
                findNavController().navigate(AppNavigationDirections.navigateToLoginScreen())
                return true
            }
            android.R.id.home ->{
                findNavController().navigateUp()
                return true
            }
        }

        return false
    }

    private fun setupObservers() {
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

        viewModel.repositoryInfo.onEach {
            initUI(it)
        }.launchIn(lifecycleScope)

        viewModel.errorFlow.onEach {
            // FIXME как и в авторизации - ненадежно :(
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

    private fun initUI(source: GitHubRepositoryModel?) {
        if (source != null) {
            bindRepositoryInfo(source)
        }
    }

    private fun bindRepositoryInfo(source: GitHubRepositoryModel) {
        with(binding) {
            with(source.license?.spdxID){
                if (this.isNullOrEmpty()){
                    licenseType.text = getString(R.string.repo_info_license_type)
                } else {
                    licenseType.text = this
                }
            }

            repositoryLinkButton.text = source.htmlURL?.substring(8, source.htmlURL.length) ?: ""
            starsCount.text = source.stargazersCount.toString()
            forksCount.text = source.forksCount.toString()
            watchersCount.text = source.watchersCount.toString()
            repositoryName.text = source.name
            repositoryDescription.text = source.description

            // FIXME а где ридми?

            repositoryLinkButton.setOnClickListener {
                if (source.htmlURL != null)
                    openInBrowser(source.htmlURL)
            }
        }
    }

    private fun openInBrowser(url: String) {
        val browser: Intent =
            Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        browser.data = Uri.parse(url)

        startActivity(browser)
    }

    override fun setNormalState() {
        with(binding) {
            errorBlock.visibility = View.GONE
            userUiGroup.visibility = View.VISIBLE

            progressBar.visibility = View.GONE
        }
    }

    override fun setLoadingState() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            licenseSectionGroup.visibility = View.GONE
            statsSectionGroup.visibility = View.GONE
            repositoryInfoSectionGroup.visibility = View.GONE
        }
    }

    override fun setErrorState() {
        with(binding) {
            userUiGroup.visibility = View.GONE
            errorBlock.visibility = View.VISIBLE
            progressBar.visibility = View.GONE

            reloadDataButton.setOnClickListener {
                viewModel.loadRepositoryInfo()
            }
        }
    }

    private fun setErrorMessage(message: String) {
        with(binding) {
            errorMessage.text = message
        }
    }
}