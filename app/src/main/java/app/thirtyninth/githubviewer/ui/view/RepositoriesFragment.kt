package app.thirtyninth.githubviewer.ui.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavAction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.thirtyninth.githubviewer.R
import app.thirtyninth.githubviewer.data.models.LanguageColor
import app.thirtyninth.githubviewer.data.network.Status
import app.thirtyninth.githubviewer.databinding.RepositoriesFragmentBinding
import app.thirtyninth.githubviewer.ui.adapters.RepositoryListAdapter
import app.thirtyninth.githubviewer.ui.viewmodel.RepositoriesViewModel
import app.thirtyninth.githubviewer.utils.StorageUtil
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import java.util.HashMap

@AndroidEntryPoint
class RepositoriesFragment : Fragment() {

    companion object {
        fun newInstance() = RepositoriesFragment()
    }

    private val viewModel: RepositoriesViewModel by viewModels()
    private val binding: RepositoriesFragmentBinding by viewBinding(CreateMethod.INFLATE)

    private lateinit var listAdapter: RepositoryListAdapter

    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initApp()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUIComponents()
        setupObservers()
    }

    private fun initApp() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isLoggedIn
                .onEach {
                    if (!it) {
                        //val action = findNavController().graph.getAction(R.id.navigate_toLoginScreen)
                        findNavController().navigate(R.id.navigate_toLoginScreen)
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
            it.name?.let { it1 -> showToast(it1) }
        }

        with(binding) {
            rvRepositoryList.apply {
                setHasFixedSize(true)
                addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                adapter = listAdapter
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.repositoryList
                .onEach {
                    when (it.status) {
                        Status.SUCCESS -> {
                            binding.progressHorizontal.visibility = View.GONE
                            binding.rvRepositoryList.visibility = View.VISIBLE

                            (binding.rvRepositoryList.adapter as RepositoryListAdapter).submitList(
                                it.data
                            )
                        }
                        Status.LOADING -> {
                            binding.progressHorizontal.visibility = View.VISIBLE
                        }
                        Status.ERROR -> {
                            //showToast("ERROR " + it.message)
                            it.message?.let { it1 -> Log.e("RETROFIT", it1) }
                        }
                    }
                }.collect()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}