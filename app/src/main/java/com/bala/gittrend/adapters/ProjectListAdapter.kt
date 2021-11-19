package com.bala.gittrend.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bala.gittrend.R
import com.bala.gittrend.databinding.ProjectItemBinding
import com.bala.gittrend.models.ProjectInfoParsed
import com.bumptech.glide.Glide
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ProjectListAdapter @Inject constructor(@ActivityContext val context: Context) :
    ListAdapter<ProjectInfoParsed, RecyclerView.ViewHolder>(DiffCallBackProjectItems()) {

    private var expandedItemPosition = -1
    private var previousExpandedItemPosition = -1

    inner class ProjectItemViewHolder(val viewBinding: ProjectItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        init {
            viewBinding.root.setOnClickListener {
                if (bindingAdapterPosition == expandedItemPosition) {
                    expandedItemPosition = -1
                    previousExpandedItemPosition = bindingAdapterPosition
                    notifyItemChanged(previousExpandedItemPosition)
                } else {
                    previousExpandedItemPosition = expandedItemPosition
                    expandedItemPosition = bindingAdapterPosition
                    notifyItemChanged(previousExpandedItemPosition)
                    notifyItemChanged(expandedItemPosition)
                }
            }
        }

        fun bind() {
            val projectItem = getItem(bindingAdapterPosition)
            viewBinding.apply {
                Glide.with(context)
                    .load(projectItem.projectOwnerDetails.avatarUrl)
                    .placeholder(R.mipmap.owner_photo_place_holder)
                    .into(ownerPhoto)
                projectOwnerName.text = projectItem.projectOwnerDetails.ownerName
                projectName.text = projectItem.name

                description.text = projectItem.description + "(" + projectItem.projectUrl + ")"

                if (projectItem.language != null) {
                    language.desc.text = projectItem.language
                } else {
                    language.root.isVisible = false
                }

                stars.icon.setImageResource(R.mipmap.star_yellow)
                stars.desc.text = projectItem.starGazersCount.toString()

                forks.icon.setImageResource(R.mipmap.fork_black)
                forks.desc.text = projectItem.forksCount.toString()

                if (bindingAdapterPosition == expandedItemPosition) {
                    setExpandedState(this)
                } else {
                    setCollapsedState(this)
                }
            }
        }
    }

    private fun setExpandedState(viewBinding: ProjectItemBinding) {
        viewBinding.apply {
            description.isVisible = true
            highlightsGroup.isVisible = true
            expandedDivider.isVisible = true
            collapsedDivider.isVisible = false
        }
    }

    private fun setCollapsedState(viewBinding: ProjectItemBinding) {
        viewBinding.apply {
            description.isVisible = false
            highlightsGroup.isVisible = false
            expandedDivider.isVisible = false
            collapsedDivider.isVisible = true
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is ProjectItemViewHolder) {
            setExpandedState(holder.viewBinding)
        }
        super.onViewRecycled(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProjectItemViewHolder(
            ProjectItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProjectItemViewHolder) {
            holder.bind()
        }
    }

    class DiffCallBackProjectItems : DiffUtil.ItemCallback<ProjectInfoParsed>() {
        override fun areItemsTheSame(oldItem: ProjectInfoParsed, newItem: ProjectInfoParsed) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ProjectInfoParsed, newItem: ProjectInfoParsed) =
            oldItem == newItem
    }

}