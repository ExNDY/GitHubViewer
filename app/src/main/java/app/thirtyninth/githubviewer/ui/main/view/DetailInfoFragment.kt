package app.thirtyninth.githubviewer.ui.main.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.thirtyninth.githubviewer.AppNavigationDirections
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.models.Readme
import app.thirtyninth.githubviewer.databinding.DetailInfoFragmentBinding
import app.thirtyninth.githubviewer.ui.main.viewmodel.DetailInfoViewModel
import app.thirtyninth.githubviewer.ui.main.viewmodel.DetailInfoViewModel.Action
import app.thirtyninth.githubviewer.ui.main.viewmodel.DetailInfoViewModel.DetailInfoScreenState
import app.thirtyninth.githubviewer.ui.main.viewmodel.DetailInfoViewModel.ReadmeState
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.SimpleEntry
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        viewModel.state.onEach { state ->
            handleState(state)
        }.launchIn(lifecycleScope)

        viewModel.readmeState.onEach { state ->
            handleReadmeState(state)
        }.launchIn(lifecycleScope)

        viewModel.action.onEach { action ->
            handleAction(action)
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun bindReadmeData(source: Readme) {
        val markwon = Markwon.builder(requireContext()).build()

        val markwonAdapter = MarkwonAdapter.builderTextViewIsRoot(R.layout.markdown_default_layout)
            .include(FencedCodeBlock::class.java, SimpleEntry.create(R.layout.markdown_view_layout, R.id.code_text_view))
            .build()

        with(binding) {
            markdown.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = markwonAdapter
            }
            readmeBlockHeader.text = source.name
        }

        //FIXME
        markwonAdapter.setMarkdown(markwon, source.download_url)
        markwonAdapter.notifyDataSetChanged()
    }

    private fun bindRepositoryInfo(source: GitHubRepository) {
        with(binding) {
            source.license?.spdxId.also { spdxId ->
                if (spdxId.isNullOrEmpty()){
                    licenseType.text = getString(R.string.repo_info_license_type)
                } else {
                    licenseType.text = spdxId
                }
            }

            repositoryLinkButton.text = source.htmlURL?.substring(8, source.htmlURL.length).orEmpty()
            starsCount.text = source.stargazersCount.toString()
            forksCount.text = source.forksCount.toString()
            watchersCount.text = source.watchersCount.toString()
            repositoryName.text = source.name

            repositoryLinkButton.setOnClickListener {
                if (source.htmlURL != null){
                    openInBrowser(source.htmlURL)
                }
            }
        }
    }

    //TODO Учесть замечания и сделать учитывая тонкости API 30lvl
    private fun openInBrowser(url: String) {
        val browser: Intent =
            Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        browser.data = Uri.parse(url)

        startActivity(browser)
    }

    private fun logout() = AlertDialog.Builder(context)
        .setTitle(getString(R.string.logout_dialog_title))
        .setMessage(getString(R.string.logout_dialog_message))
        .setPositiveButton(getString(R.string.logout_dialog_positive)) { _, _ ->
            viewModel.logout()
        }
        .setNegativeButton(getString(R.string.logout_dialog_negative), null)
        .show()

    private fun setLoadingState() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            licenseSectionGroup.visibility = View.GONE
            statsSectionGroup.visibility = View.GONE
            repositoryInfoSectionGroup.visibility = View.GONE
        }
    }

    private fun setLoadedState(gitHubRepo:GitHubRepository, readmeState: ReadmeState){
        with(binding) {
            errorBlock.visibility = View.GONE
            userUiGroup.visibility = View.VISIBLE

            progressBar.visibility = View.GONE
        }

        bindRepositoryInfo(gitHubRepo)
        handleReadmeState(readmeState)
    }

    private fun setErrorState(error:String){
        with(binding) {
            userUiGroup.visibility = View.GONE
            errorBlock.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            errorMessage.text = error

            reloadDataButton.setOnClickListener {
                viewModel.retryButtonClicked()
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

    private fun handleState(state:DetailInfoScreenState){
        when(state){
            DetailInfoScreenState.Loading -> setLoadingState()
            is DetailInfoScreenState.Loaded -> setLoadedState(state.githubRepo, state.readmeState)
            is DetailInfoScreenState.Error -> setErrorState(state.error.getString(requireContext()))
        }
    }

    //TODO Реализовать ридми
    private fun handleReadmeState(state:ReadmeState){
        when(state){
            ReadmeState.Loading ->{}
            is ReadmeState.Loaded ->{}
            is ReadmeState.Error ->{}
            is ReadmeState.Empty ->{}
        }
    }
}