package com.community.mingle.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.community.mingle.databinding.ItemHomeHotPostBinding
import com.community.mingle.model.post.HomeHotPost

class HomeHotPostListAdapter(
    private val onItemClick: (HomeHotPost, Int) -> Unit,
    private val onCancelBlindClick: (HomeHotPost, Int) -> Unit,
) : ListAdapter<HomeHotPost, HomeHotPostListAdapter.HomeHotPostViewHolder>(
    object : DiffUtil.ItemCallback<HomeHotPost>() {
        override fun areItemsTheSame(oldItem: HomeHotPost, newItem: HomeHotPost): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: HomeHotPost, newItem: HomeHotPost): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHotPostViewHolder {
        return HomeHotPostViewHolder(
            binding = ItemHomeHotPostBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClick = onItemClick,
            onCancelBlindClick = onCancelBlindClick,
        )
    }

    override fun onBindViewHolder(holder: HomeHotPostViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class HomeHotPostViewHolder(
        private val binding: ItemHomeHotPostBinding,
        private val onItemClick: (HomeHotPost, Int) -> Unit,
        private val onCancelBlindClick: (HomeHotPost, Int) -> Unit,
    ) : ViewHolder(binding.root) {

        fun bind(item: HomeHotPost) {
            binding.item = item

            if (item.blinded) {
                blindedPost(item)
            } else if (item.reported) {
                reportedPost(item)
            } else {
                normalPost(item)
            }

            binding.executePendingBindings()
        }

        private fun blindedPost(item: HomeHotPost) {
            binding.likeimg.visibility = View.INVISIBLE
            binding.commentimg.visibility = View.INVISIBLE
            binding.photo.visibility = View.INVISIBLE
            binding.contenttext.visibility = View.INVISIBLE
            binding.titletext.visibility = View.INVISIBLE
            binding.liketext.visibility = View.INVISIBLE
            binding.commenttext.visibility = View.INVISIBLE
            binding.anonymous.visibility = View.INVISIBLE
            binding.time.visibility = View.INVISIBLE
            binding.dot.visibility = View.INVISIBLE

            binding.blindedText.visibility = View.VISIBLE
            binding.cancelBlindTv.visibility = View.VISIBLE

            binding.cancelBlindTv.setOnClickListener {
                onCancelBlindClick(item, absoluteAdapterPosition)
            }

            binding.root.setOnClickListener {
                onItemClick(item, absoluteAdapterPosition)
            }
        }

        private fun reportedPost(item: HomeHotPost) {
            binding.likeimg.visibility = View.INVISIBLE
            binding.commentimg.visibility = View.INVISIBLE
            binding.photo.visibility = View.INVISIBLE
            binding.contenttext.visibility = View.INVISIBLE
            binding.titletext.visibility = View.INVISIBLE
            binding.liketext.visibility = View.INVISIBLE
            binding.commenttext.visibility = View.INVISIBLE
            binding.anonymous.visibility = View.INVISIBLE
            binding.time.visibility = View.INVISIBLE
            binding.dot.visibility = View.INVISIBLE

            binding.blindedText.visibility = View.VISIBLE
            binding.blindedText.text = item.title

            binding.root.setOnClickListener {
                onItemClick(item, absoluteAdapterPosition)
            }
        }

        private fun normalPost(item: HomeHotPost) {
            val fileAttached = item.fileAttached
            if (fileAttached) {
                binding.photo.visibility = View.VISIBLE
            } else {
                binding.photo.visibility = View.GONE
            }

            binding.likeimg.visibility = View.VISIBLE
            binding.commentimg.visibility = View.VISIBLE
            binding.contenttext.visibility = View.VISIBLE
            binding.titletext.visibility = View.VISIBLE
            binding.liketext.visibility = View.VISIBLE
            binding.commenttext.visibility = View.VISIBLE
            binding.anonymous.visibility = View.VISIBLE
            binding.time.visibility = View.VISIBLE
            binding.dot.visibility = View.VISIBLE

            binding.blindedText.visibility = View.GONE
            binding.cancelBlindTv.visibility = View.GONE

            binding.cancelBlindTv.setOnClickListener {
                onCancelBlindClick(item, absoluteAdapterPosition)
            }

            binding.root.setOnClickListener {
                onItemClick(item, absoluteAdapterPosition)
            }
        }
    }
}