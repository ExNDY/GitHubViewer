package app.thirtyninth.githubviewer.ui.adapters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.databinding.RepositoriesListItemBinding
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive

class RepositoryListAdapter(
    // FIXME почему бы сразу не указать private val ?
    colors: JsonObject,
    val onItemClicked: (GitHubRepositoryModel) -> Unit
) : ListAdapter<GitHubRepositoryModel, RepositoryListAdapter.RepositoryListViewHolder>(
    RepositoryListDiffCallback()
) {
    private val languageColors: Map<String, Color> = colors.mapValues { (_, colorString) ->
        Color.parseColor(colorString.jsonPrimitive.content).let { Color.valueOf(it) }
    }

    // FIXME для чего именно inner класом сделано?
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

        fun bind(repository: GitHubRepositoryModel) {
            bindName(repository.name)

            bindLanguage(repository.language)
            bindDescription(repository.description)
        }

        private fun bindName(name: String?) {
            itemBinding.repositoryName.text = name
        }

        private fun bindLanguage(language: String?) {
            if (language.isNullOrEmpty()) {
                itemBinding.language.visibility = View.GONE
            } else {
                itemBinding.language.setTextColor(
                    Color.parseColor(
                        getHEX(language)
                    )
                )

                itemBinding.language.text = language
            }

        }

        fun bindDescription(description: String?) {
            if (description.isNullOrEmpty()) {
                itemBinding.repositoryDescription.visibility = View.GONE
            } else {
                itemBinding.repositoryDescription.text = description
            }

        }

        private fun getHEX(language: String): String {
            return languageColors[language]?.let { Json.decodeFromJsonElement<String>(it) }
                ?: "#FFFFFF"
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

    // FIXME почему только на описание реакция обновления есть? ненадежно выглядит
//    override fun onBindViewHolder(
//        holder: RepositoryListViewHolder,
//        position: Int,
//        payloads: MutableList<Any>
//    ) {
//        if (payloads.isEmpty()) {
//            super.onBindViewHolder(holder, position, payloads)
//        } else {
//            val bundle = payloads[0] as Bundle
//
//            for (key: String in bundle.keySet()) {
//                if (key == PAYLOAD_DESCRIPTION) {
//                    bundle.getString(PAYLOAD_DESCRIPTION)?.let { holder.bindDescription(it) }
//                }
//            }
//        }
//        super.onBindViewHolder(holder, position, payloads)
//    }

    override fun getItemCount(): Int = currentList.size
}

private const val PAYLOAD_DESCRIPTION = "PAYLOAD_DESCRIPTION"

// FIXME почему только на описание реакция обновления есть? ненадежно выглядит.
//  в текущем случае надежнее вообще пейлоад не получать
private class RepositoryListDiffCallback : DiffUtil.ItemCallback<GitHubRepositoryModel>() {
    override fun getChangePayload(
        oldItem: GitHubRepositoryModel,
        newItem: GitHubRepositoryModel
    ): Any? = null

    // FIXME почему сравнение по имени? у нас одинаковое имя может быть, надежности тут нет. вот ссылка - уникальна.
    //  а главное это id разумеется
    override fun areItemsTheSame(
        oldItem: GitHubRepositoryModel,
        newItem: GitHubRepositoryModel
    ): Boolean =
        oldItem.name == newItem.name


    override fun areContentsTheSame(
        oldItem: GitHubRepositoryModel,
        newItem: GitHubRepositoryModel
    ): Boolean =
        oldItem == newItem
}