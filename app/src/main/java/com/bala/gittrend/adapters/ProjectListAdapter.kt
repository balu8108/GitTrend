package com.bala.gittrend.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
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

    inner class ProjectItemViewHolder(val viewBinding: ProjectItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
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
            val projectItem = getItem(position)
            holder.viewBinding.apply {
                Glide.with(context)
                    .load(projectItem.projectOwnerDetails.avatarUrl)
                    .placeholder(R.mipmap.owner_photo_place_holder)
                    .into(ownerPhoto)
                projectOwnerName.text = projectItem.projectOwnerDetails.ownerName
                projectName.text = projectItem.name
            }
        }
    }

    class DiffCallBackProjectItems : DiffUtil.ItemCallback<ProjectInfoParsed>() {
        override fun areItemsTheSame(oldItem: ProjectInfoParsed, newItem: ProjectInfoParsed) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ProjectInfoParsed, newItem: ProjectInfoParsed) =
            oldItem == newItem
    }

}