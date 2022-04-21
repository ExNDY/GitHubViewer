package app.thirtyninth.githubviewer.ui.view

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import app.thirtyninth.githubviewer.databinding.DetailInfoFragmentBinding
import app.thirtyninth.githubviewer.ui.viewmodel.DetailInfoViewModel
import app.thirtyninth.githubviewer.ui.viewmodel.DetailInfoViewModel.Action
import app.thirtyninth.githubviewer.ui.viewmodel.DetailInfoViewModel.ReadmeState
import app.thirtyninth.githubviewer.ui.viewmodel.DetailInfoViewModel.ScreenState
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
        binding.markdown.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = markwonAdapter
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
    private fun handleReadmeLoadedState(state: ReadmeState) {

        binding.readmeBlockHeader.text = if (state is ReadmeState.Loaded) {
            state.readmeDetail.name
        } else {
            getString(R.string.empty_readme)
        }

        val markdownString: String = if (state is ReadmeState.Loaded) {
            state.markdown
        } else {
            ""
        }

        markwonAdapter.setMarkdown(markwon, markdownString)
        markwonAdapter.notifyDataSetChanged()
    }

    private fun handleDetailsLoadedState(state: ScreenState) {
        with(binding) {

            licenseType.text = if (state is ScreenState.Loaded) {
                val licenseType: String? = state.githubRepo.license?.spdxId

                if (licenseType.isNullOrEmpty()) {
                    getString(R.string.repo_info_license_type)
                } else {
                    licenseType
                }
            } else {
                getString(R.string.repo_info_license_type)
            }

            repositoryLinkButton.text = if (state is ScreenState.Loaded) {
                state.githubRepo.htmlURL?.substring(8, state.githubRepo.htmlURL.length).orEmpty()
            } else {
                ""
            }

            starsCount.text = if (state is ScreenState.Loaded) {
                state.githubRepo.stargazersCount.toString()
            } else {
                "-"
            }

            forksCount.text = if (state is ScreenState.Loaded) {
                state.githubRepo.forksCount.toString()
            } else {
                "-"
            }

            watchersCount.text = if (state is ScreenState.Loaded) {
                state.githubRepo.watchersCount.toString()
            } else {
                "-"
            }

            repositoryName.text = if (state is ScreenState.Loaded) {
                state.githubRepo.name
            } else {
                ""
            }

            repositoryDescription.text = if (state is ScreenState.Loaded) {
                state.githubRepo.description
            } else {
                ""
            }

            val listener: View.OnClickListener? = if (state is ScreenState.Loaded) {
                View.OnClickListener {
                    if (state.githubRepo.htmlURL != null) {
                        openInBrowser(state.githubRepo.htmlURL)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.url_not_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else null

            repositoryLinkButton.setOnClickListener(listener)
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

    private fun handleErrorState(state: ScreenState) {
        val title: String = if (state is ScreenState.Error) {
            state.exceptionBundle.title.getString(requireContext())
        } else {
            ""
        }

        val message: String = if (state is ScreenState.Error) {
            state.exceptionBundle.message.getString(requireContext())
        } else {
            ""
        }

        val errorDrawable: Drawable? = if (state is ScreenState.Error) {
            ContextCompat.getDrawable(requireContext(), state.exceptionBundle.imageResId)
        } else {
            null
        }
        val colorId = if (state is ScreenState.Error) {
            state.exceptionBundle.colorResId
        } else {
            R.color.white
        }

        with(binding.blockError) {
            errorTitle.setTextColor(
                ContextCompat.getColor(requireContext(), colorId)
            )

            errorImg.setImageDrawable(errorDrawable)
            errorImg.visibility =
                if (state is ScreenState.Error) View.VISIBLE else View.GONE
            errorTitle.text = title
            errorMessage.text = message

            val listener: View.OnClickListener? = if (state is ScreenState.Error) {
                View.OnClickListener {
                    viewModel.retryButtonClicked()
                }
            } else null

            retryButton.setOnClickListener(listener)
        }
    }

    private fun handleReadmeErrorState(state: ReadmeState) {

        val title: String = if (state is ReadmeState.Error) {
            state.exceptionBundle.title.getString(requireContext())
        } else {
            ""
        }

        val message: String = if (state is ReadmeState.Error) {
            state.exceptionBundle.message.getString(requireContext())
        } else {
            ""
        }

        val errorDrawable: Drawable? = if (state is ReadmeState.Error) {
            ContextCompat.getDrawable(requireContext(), state.exceptionBundle.imageResId)
        } else {
            null
        }
        val colorId = if (state is ReadmeState.Error) {
            state.exceptionBundle.colorResId
        } else {
            R.color.white
        }

        with(binding.blockReadmeError) {
            errorTitle.setTextColor(
                ContextCompat.getColor(requireContext(), colorId)
            )

            errorImg.setImageDrawable(errorDrawable)
            errorImg.visibility =
                if (state is ReadmeState.Error) View.VISIBLE else View.GONE
            errorTitle.text = title
            errorMessage.text = message

            val listener: View.OnClickListener? = if (state is ReadmeState.Error) {
                View.OnClickListener {
                    viewModel.retryReadmeButtonClicked()
                }
            } else null

            retryButton.setOnClickListener(listener)
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

        handleDetailsLoadedState(state)

        handleErrorState(state)
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

        handleReadmeLoadedState(state)

        handleReadmeErrorState(state)
    }
}