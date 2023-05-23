package ru.dev.fabled.repository.repository.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.dev.fabled.core.ListItemClickListener
import ru.dev.fabled.domain.model.RepositoryContent
import ru.dev.fabled.repository.R
import ru.dev.fabled.repository.databinding.ItemRepositoryContentBinding

class RepositoryContentListAdapter(
    private val itemClickListener: ListItemClickListener<RepositoryContent>
) : ListAdapter<RepositoryContent, RepositoryContentListAdapter.RepositoryContentItemViewHolder>(
    RepositoryContentDiffUtil
) {

    inner class RepositoryContentItemViewHolder(
        private val binding: ItemRepositoryContentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(content: RepositoryContent) = with(binding) {
            val imageDrawable = when (content) {
                is RepositoryContent.File -> R.drawable.ic_file
                is RepositoryContent.Folder -> R.drawable.ic_folder
            }

            contentIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    contentIcon.context,
                    imageDrawable
                )
            )
            contentName.text = content.contentName

            root.setOnClickListener { itemClickListener.onItemClick(content) }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepositoryContentItemViewHolder {
        val binding = ItemRepositoryContentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepositoryContentItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepositoryContentItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    private object RepositoryContentDiffUtil : DiffUtil.ItemCallback<RepositoryContent>() {
        override fun areItemsTheSame(
            oldItem: RepositoryContent,
            newItem: RepositoryContent
        ): Boolean {
            return oldItem.contentName == newItem.contentName
        }

        override fun areContentsTheSame(
            oldItem: RepositoryContent,
            newItem: RepositoryContent
        ): Boolean {
            return oldItem == newItem
        }
    }

}