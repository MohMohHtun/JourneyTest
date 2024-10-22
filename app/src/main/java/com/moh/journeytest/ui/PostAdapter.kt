package com.moh.journeytest.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moh.journeytest.R
import com.moh.journeytest.databinding.PostLayoutBinding
import com.moh.journeytest.model.Post

class PostAdapter(private val onItemClickListener: (Post) -> Unit) :
    ListAdapter<Post, PostAdapter.PostViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding: PostLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.post_layout,
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(
            item,
            onItemClickListener
        )  // Pass the post and the click listener to the ViewHolder
    }

    class PostViewHolder(private val binding: PostLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, onItemClickListener: (Post) -> Unit) {
            binding.post = post
            binding.executePendingBindings()  // Executes the binding immediately

            // Set a click listener for the post
            binding.root.setOnClickListener {
                onItemClickListener(post)  // Call the click listener with the clicked item
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldPost: Post, newPost: Post): Boolean =
            oldPost.id == newPost.id

        override fun areContentsTheSame(oldPost: Post, newPost: Post): Boolean = oldPost == newPost
    }
}