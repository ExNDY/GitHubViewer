package app.thirtyninth.githubviewer.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.thirtyninth.githubviewer.databinding.RepositoriesFragmentBinding
import app.thirtyninth.githubviewer.databinding.RepositoryInfoFragmentBinding
import app.thirtyninth.githubviewer.ui.viewmodel.LoginViewModel
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepositoryInfoFragment : Fragment() {

    companion object {
        fun newInstance() = RepositoryInfoFragment()
    }

    private val viewModel: LoginViewModel by viewModels()
    private val binding: RepositoryInfoFragmentBinding by viewBinding(CreateMethod.INFLATE)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}