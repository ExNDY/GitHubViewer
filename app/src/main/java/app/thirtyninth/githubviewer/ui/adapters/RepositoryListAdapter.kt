package app.thirtyninth.githubviewer.ui.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.databinding.RepositoriesListItemBinding

class RepositoryListAdapter(
    val onItemClicked: (GitHubRepository) -> Unit
) : ListAdapter<GitHubRepository,RepositoryListAdapter.RepositoryListViewHolder>(RepositoryListDiffCallback()) {

    inner class RepositoryListViewHolder(
        private val itemBinding: RepositoriesListItemBinding,
        onItemClicked: (Int) -> Unit
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        init {
            itemView.setOnClickListener {
                onItemClicked(bindingAdapterPosition)
            }
        }

        fun bind(repository: GitHubRepository) {
            repository.name?.let { bindName(it) }
            repository.language?.let { bindLanguage(it) }

            if (repository.description.isNullOrEmpty()){
                itemBinding.repositoryDescription.visibility = View.GONE
            } else {
                bindDescription(repository.description)
            }


        }

        private fun bindName(name: String) {
            itemBinding.repositoryName.text = name
        }

        private fun bindLanguage(language: String) {
            itemBinding.language.text = language
        }

        fun bindDescription(description: String) {
            if (description.isEmpty()) {
                itemBinding.repositoryDescription.visibility = View.GONE
            } else {
                itemBinding.repositoryDescription.text = description
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryListViewHolder {
        val itemBinding =
            RepositoriesListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RepositoryListViewHolder(itemBinding) {
            onItemClicked(getItem(it))
        }
    }

    override fun onBindViewHolder(holder: RepositoryListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun onBindViewHolder(
        holder: RepositoryListViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val bundle = payloads[0] as Bundle

            for (key:String in bundle.keySet()){
                if (key == PAYLOAD_DESCRIPTION){
                    bundle.getString(PAYLOAD_DESCRIPTION)?.let { holder.bindDescription(it) }
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int = currentList.size
}

private const val PAYLOAD_DESCRIPTION = "PAYLOAD_DESCRIPTION"

private class RepositoryListDiffCallback : DiffUtil.ItemCallback<GitHubRepository>(){
    override fun getChangePayload(oldItem: GitHubRepository, newItem: GitHubRepository): Any? {
        if (newItem.name == oldItem.name) {
            return if (newItem.description == oldItem.description){
                super.getChangePayload(oldItem, newItem)
            } else {
                val bundle = Bundle()

                bundle.putString(PAYLOAD_DESCRIPTION, newItem.description)
                bundle
            }
        }

        return super.getChangePayload(oldItem, newItem)
    }

    override fun areItemsTheSame(oldItem: GitHubRepository, newItem: GitHubRepository): Boolean =
        oldItem.name == newItem.name


    override fun areContentsTheSame(oldItem: GitHubRepository, newItem: GitHubRepository): Boolean =
        oldItem == newItem
}