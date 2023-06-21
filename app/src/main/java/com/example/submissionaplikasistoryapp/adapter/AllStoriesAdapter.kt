package com.example.submissionaplikasistoryapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionaplikasistoryapp.databinding.ItemStoryBinding
import com.example.submissionaplikasistoryapp.response.ListStoryItem

class AllStoriesAdapter :
    PagingDataAdapter<ListStoryItem, AllStoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data, onItemClickCallback)
        }
    }

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem, onItemClickCallback: OnItemClickCallback) {
            binding.tvItemName.text = data.name
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.ivItemPhoto)

            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(data)
        }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(story: ListStoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}