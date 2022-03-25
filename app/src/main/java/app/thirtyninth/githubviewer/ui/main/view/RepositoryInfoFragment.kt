package app.thirtyninth.githubviewer.ui.main.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.thirtyninth.githubviewer.AppNavigationDirections
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.models.Readme
import app.thirtyninth.githubviewer.databinding.RepositoryInfoFragmentBinding
import app.thirtyninth.githubviewer.ui.main.viewmodel.RepositoryInfoViewModel
import app.thirtyninth.githubviewer.ui.main.viewmodel.RepositoryInfoViewModel.Action
import app.thirtyninth.githubviewer.utils.mapExceptionToMessage
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class RepositoryInfoFragment : Fragment() {
    private val viewModel: RepositoryInfoViewModel by viewModels()
    private val binding: RepositoryInfoFragmentBinding by viewBinding(CreateMethod.INFLATE)
    private val args: RepositoryInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        setupToolbar(args.repositoryName)
    }

    private fun setupToolbar(title: String) {
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

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            toolbar.title = title
        }
    }

    private fun setupObservers() {
        viewModel.actions.onEach { action ->
            handleAction(action)
        }.launchIn(lifecycleScope)

        viewModel.repositoryInfo.onEach { repository ->
            initUI(repository)
        }.launchIn(lifecycleScope)

        viewModel.readme.onEach { readme ->
            initReadme(readme)
        }.launchIn(lifecycleScope)

        viewModel.readmeFile.onEach { readmeFile ->
            addReadmeFile(readmeFile)
        }.launchIn(lifecycleScope)
    }

    private fun initUI(source: GitHubRepositoryModel?) {
        if (source != null) {
            bindRepositoryInfo(source)
        }
    }

    private fun initReadme(source: Readme?) {
        if (source != null) {
            bindReadmeData(source)
        }
    }

    private fun addReadmeFile(source: String) {
        if (source.isNotEmpty()) {
            with(binding) {
                //markdownView.setMarkDownText(source)
            }
        }
    }

    private fun bindReadmeData(source: Readme) {
        with(binding) {
            readmeBlockHeader.text = source.name
        }
    }

    private fun bindRepositoryInfo(source: GitHubRepositoryModel) {
        with(binding) {
            with(source.license?.spdxId) {
                if (this.isNullOrEmpty()) {
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

    private fun setNormalState() {
        with(binding) {
            errorBlock.visibility = View.GONE
            userUiGroup.visibility = View.VISIBLE

            progressBar.visibility = View.GONE
        }
    }

    private fun setLoadingState() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            licenseSectionGroup.visibility = View.GONE
            statsSectionGroup.visibility = View.GONE
            repositoryInfoSectionGroup.visibility = View.GONE
        }
    }

    private fun setErrorState(throwable: Throwable) {
        val message = mapExceptionToMessage(throwable, requireContext().resources)

        with(binding) {
            userUiGroup.visibility = View.GONE
            errorBlock.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            errorMessage.text = message
            reloadDataButton.setOnClickListener {
                viewModel.loadRepositoryInfo()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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