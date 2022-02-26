package app.thirtyninth.githubviewer.ui.base

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseFragment : Fragment(), app.thirtyninth.githubviewer.ui.UIState {

    override fun setNormalState() {

    }

    override fun setLoadingState() {

    }

    override fun setErrorState() {

    }
}