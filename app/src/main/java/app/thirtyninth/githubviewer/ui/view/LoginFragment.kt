package app.thirtyninth.githubviewer.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.thirtyninth.githubviewer.data.network.Status
import app.thirtyninth.githubviewer.databinding.LoginFragmentBinding
import app.thirtyninth.githubviewer.ui.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel:LoginViewModel by viewModels()
    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            viewModel.loadUser()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.user
                .onEach {
                    when(it.status){
                        Status.SUCCESS -> {
                            showToast("SUCCESS " + (it.data?.login ?: ""))
                        }
                        Status.LOADING -> {
                            showToast("LOADING")
                        }
                        Status.ERROR -> {

                        }
                    }
                }.collect()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun showToast(message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}