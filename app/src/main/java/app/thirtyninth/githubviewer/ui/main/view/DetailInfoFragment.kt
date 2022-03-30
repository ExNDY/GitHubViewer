package app.thirtyninth.githubviewer.ui.main.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import app.thirtyninth.githubviewer.extentions.getCoilPlugin
import app.thirtyninth.githubviewer.ui.main.viewmodel.DetailInfoViewModel
import app.thirtyninth.githubviewer.ui.main.viewmodel.DetailInfoViewModel.Action
import app.thirtyninth.githubviewer.ui.main.viewmodel.DetailInfoViewModel.ReadmeState
import app.thirtyninth.githubviewer.ui.main.viewmodel.DetailInfoViewModel.ScreenState
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.SimpleEntry
import io.noties.markwon.simple.ext.SimpleExtPlugin
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.commonmark.node.FencedCodeBlock

//TODO rewrite logic and naming
@AndroidEntryPoint
class DetailInfoFragment : Fragment() {
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

        setupObservers()

        setupToolbar(args.repositoryName)
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

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.onEach { action ->
                    handleAction(action)
                }.launchIn(lifecycleScope)

                viewModel.readmeState.onEach { state ->
                    handleReadmeState(state)
                }.launchIn(lifecycleScope)

                viewModel.state.onEach { state ->
                    handleState(state)
                }.launchIn(lifecycleScope)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupReadmeMd(readme: String, readmeDetail: Readme) {
        val markwon = Markwon.builder(requireContext())
            .usePlugin(MarkwonInlineParserPlugin.create())
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(requireContext()))
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(getCoilPlugin(requireContext()))
            .usePlugin(SimpleExtPlugin.create())
            .build()

        val markwonAdapter = MarkwonAdapter.builderTextViewIsRoot(R.layout.markdown_default_layout)
            .include(
                FencedCodeBlock::class.java,
                SimpleEntry.create(R.layout.markdown_view_layout, R.id.code_text_view)
            )
            .build()

        with(binding) {
            markdown.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = markwonAdapter
            }

            readmeBlockHeader.text = readmeDetail.name
        }

        markwonAdapter.setMarkdown(markwon, readme)
        markwonAdapter.notifyDataSetChanged()
    }

    private fun handleRepositoryDetail(gitHubRepository: GitHubRepository) {
        with(binding) {
            gitHubRepository.license?.spdxId.also { spdxId ->
                if (spdxId.isNullOrEmpty()) {
                    licenseType.text = getString(R.string.repo_info_license_type)
                } else {
                    licenseType.text = spdxId
                }
            }

            repositoryLinkButton.text =
                gitHubRepository.htmlURL?.substring(8, gitHubRepository.htmlURL.length).orEmpty()
            starsCount.text = gitHubRepository.stargazersCount.toString()
            forksCount.text = gitHubRepository.forksCount.toString()
            watchersCount.text = gitHubRepository.watchersCount.toString()
            repositoryName.text = gitHubRepository.name
            repositoryDescription.text = gitHubRepository.description

            repositoryLinkButton.setOnClickListener {
                if (gitHubRepository.htmlURL != null) {
                    openInBrowser(gitHubRepository.htmlURL)
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
            Log.e("OPEN_IN_BROWSER", "Browser don't found in system.", ex)
            Toast.makeText(
                context,
                getString(R.string.exception_browser_not_found),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun logout() = AlertDialog.Builder(context)
        .setTitle(getString(R.string.logout_dialog_title))
        .setMessage(getString(R.string.logout_dialog_message))
        .setPositiveButton(getString(R.string.logout_dialog_positive)) { _, _ ->
            viewModel.logout()
        }
        .setNegativeButton(getString(R.string.logout_dialog_negative), null)
        .show()

    private fun handleErrorState(exceptionBundle: ExceptionBundle) {
        val title = exceptionBundle.title.getString(requireContext())
        val message = exceptionBundle.message.getString(requireContext())
        val imageId = exceptionBundle.imageResId
        val colorId = exceptionBundle.colorResId

        with(binding) {
            blockError.errorTitle.setTextColor(resources.getColor(colorId, resources.newTheme()))
            blockError.errorTitle.text = title
            blockError.errorMessage.text = message
            blockError.errorImg.setImageResource(imageId)

            blockError.retryButton.setOnClickListener {
                viewModel.retryButtonClicked()
            }
        }
    }

    private fun handleReadmeErrorState(exceptionBundle: ExceptionBundle) {
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
                viewModel.retryLoadReadmeButtonClicked()
            }
        }
    }

    private fun routeToAuthScreen() {
        findNavController().navigate(AppNavigationDirections.navigateToLoginScreen())
    }

    private fun handleAction(action: Action) {
        when (action) {
            is Action.RouteToAuthScreen -> routeToAuthScreen()
        }
    }

    private fun handleState(state: ScreenState) {
        with(binding) {
            blockData.visibility = if (state is ScreenState.Loaded) {
                handleRepositoryDetail(state.githubRepo)
                handleReadmeState(state.readmeState)
                View.VISIBLE
            } else {
                View.GONE
            }

            blockLoading.container.visibility = if (state is ScreenState.Loading) {
                View.VISIBLE
            } else {
                View.GONE
            }

            blockError.container.visibility = if (state is ScreenState.Error) {
                handleErrorState(state.exceptionBundle)
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun handleReadmeState(state: ReadmeState) {
        with(binding) {
            markdown.visibility = if (state is ReadmeState.Loaded) {
                setupReadmeMd(state.markdown, state.readmeDetail)
                View.VISIBLE
            } else {
                View.GONE
            }

            blockReadmeLoading.visibility = if (state is ReadmeState.Loading) {
                View.VISIBLE
            } else {
                View.GONE
            }

            blockReadmeError.container.visibility = if (state is ReadmeState.Error) {
                handleReadmeErrorState(state.exceptionBundle)
                View.VISIBLE
            } else {
                View.GONE
            }

            readmeBlockHeader.text = when (state) {
                is ReadmeState.Empty -> getString(R.string.empty_readme)
                is ReadmeState.Loaded -> state.readmeDetail.name
                else -> {
                    ""
                }
            }
        }
    }
}