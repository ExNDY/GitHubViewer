package app.thirtyninth.githubviewer.ui.view

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.thirtyninth.githubviewer.AppNavigationDirections
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.models.ExceptionBundle
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.models.Readme
import app.thirtyninth.githubviewer.databinding.DetailInfoFragmentBinding
import app.thirtyninth.githubviewer.ui.viewmodel.DetailInfoViewModel
import app.thirtyninth.githubviewer.ui.viewmodel.DetailInfoViewModel.*
import app.thirtyninth.githubviewer.utils.callLogoutDialog
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.recycler.MarkwonAdapter
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DetailInfoFragment : Fragment() {
    private val TAG = DetailInfoFragment::class.java.simpleName

    @Inject
    lateinit var markwon: Markwon

    @Inject
    lateinit var markwonAdapter: MarkwonAdapter

    private val viewModel: DetailInfoViewModel by viewModels()
    private val binding: DetailInfoFragmentBinding by viewBinding(CreateMethod.INFLATE)
    private val args: DetailInfoFragmentArgs by navArgs()

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

        setupToolbar(args.repositoryName)
        setupMarkdown()
        setupObservers()
    }

    private fun setupToolbar(title: String) {
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

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            toolbar.title = title
        }
    }

    private fun setupMarkdown() {
        with(binding) {
            markdown.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = markwonAdapter
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.readmeState.collect { state ->
                    handleReadmeState(state)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setReadme(
        readme: String,
        readmeDetail: Readme
    ) {
        binding.readmeBlockHeader.text = readmeDetail.name

        markwonAdapter.setMarkdown(markwon, readme)
        markwonAdapter.notifyDataSetChanged()
    }

    private fun setRepositoryDetail(repository: GitHubRepository) {
        with(binding) {
            repository.license?.spdxId.also { spdxId ->
                if (spdxId.isNullOrEmpty()) {
                    licenseType.text = getString(R.string.repo_info_license_type)
                } else {
                    licenseType.text = spdxId
                }
            }

            repositoryLinkButton.text =
                repository.htmlURL?.substring(8, repository.htmlURL.length).orEmpty()
            starsCount.text = repository.stargazersCount.toString()
            forksCount.text = repository.forksCount.toString()
            watchersCount.text = repository.watchersCount.toString()
            repositoryName.text = repository.name
            repositoryDescription.text = repository.description

            repositoryLinkButton.setOnClickListener {
                if (repository.htmlURL != null) {
                    openInBrowser(repository.htmlURL)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.url_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openInBrowser(url: String) {
        val browser: Intent =
            Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        browser.data = Uri.parse(url)

        try {
            startActivity(browser)
        } catch (ex: ActivityNotFoundException) {
            Timber.tag(TAG).e(ex, "OPEN_IN_BROWSER")

            Toast.makeText(
                context,
                getString(R.string.exception_browser_not_found),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun logout() = callLogoutDialog(requireContext()) { viewModel.onLogoutClicked() }

    private fun setErrorState(state: ScreenState) {
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
                blockError.errorTitle.setTextColor(resources.getColor(colorId, resources.newTheme()))
            }

            if (imageId != null) {
                blockError.errorImg.setImageResource(imageId)
            }

            blockError.errorTitle.text = title
            blockError.errorMessage.text = message

            if (state is ScreenState.Error) {
                blockError.retryButton.setOnClickListener {
                    viewModel.retryButtonClicked()
                }
            }
        }
    }

    private fun setReadmeErrorState(exceptionBundle: ExceptionBundle) {
        Timber.tag(TAG).d(exceptionBundle.toString())

        val title = exceptionBundle.title.getString(requireContext())
        val message = exceptionBundle.message.getString(requireContext())
        val imageId = exceptionBundle.imageResId
        val colorId = exceptionBundle.colorResId

        with(binding) {
            blockReadmeError.errorTitle.setTextColor(
                resources.getColor(
                    colorId,
                    resources.newTheme()
                )
            )
            blockReadmeError.errorTitle.text = title
            blockReadmeError.errorMessage.text = message
            blockReadmeError.errorImg.setImageResource(imageId)

            blockReadmeError.retryButton.setOnClickListener {
                viewModel.retryReadmeButtonClicked()
            }
        }
    }

    private fun routeToAuthScreen() {
        findNavController().navigate(AppNavigationDirections.routeToAuthScreen())
    }

    private fun handleAction(action: Action) {
        when (action) {
            is Action.RouteToAuthScreen -> routeToAuthScreen()
        }
    }

    private fun handleState(state: ScreenState) {
        with(binding) {
            blockData.visibility = if (state is ScreenState.Loaded) View.VISIBLE else View.GONE
            blockLoading.container.visibility =
                if (state is ScreenState.Loading) View.VISIBLE else View.GONE
            blockError.container.visibility =
                if (state is ScreenState.Error) View.VISIBLE else View.GONE
        }

        if (state is ScreenState.Loaded) {
            setRepositoryDetail(state.githubRepo)
        }

        if (state is ScreenState.Error) {
            setErrorState(state)
        }
    }

    private fun handleReadmeState(state: ReadmeState) {
        with(binding) {
            markdown.visibility = if (state is ReadmeState.Loaded) View.VISIBLE else View.GONE
            blockReadmeLoading.visibility =
                if (state is ReadmeState.Loading) View.VISIBLE else View.GONE
            blockReadmeError.container.visibility =
                if (state is ReadmeState.Error) View.VISIBLE else View.GONE

            readmeBlockHeader.text = when (state) {
                is ReadmeState.Empty -> getString(R.string.empty_readme)
                is ReadmeState.Loaded -> state.readmeDetail.name
                else -> {
                    ""
                }
            }
        }

        if (state is ReadmeState.Loaded) {
            setReadme(state.markdown, state.readmeDetail)
        }

        if (state is ReadmeState.Error) {
            setReadmeErrorState(state.exceptionBundle)
        }
    }
}