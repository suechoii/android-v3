package com.community.mingle.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.BR
import com.community.mingle.R
import com.community.mingle.databinding.ItemPostBinding
import com.community.mingle.databinding.ItemPostNextListLoadingBinding
import com.community.mingle.databinding.ItemPostNoMoreListBinding
import com.community.mingle.service.models.PostListItem
import com.community.mingle.service.models.PostResult

class UnivTotalListAdapter : ListAdapter<PostListItem, UnivTotalListAdapter.UnivTotalViewHolder>(
    object : DiffUtil.ItemCallback<PostListItem>() {
        override fun areItemsTheSame(oldItem: PostListItem, newItem: PostListItem): Boolean {
            return oldItem is PostResult
                    && newItem is PostResult
                    && oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: PostListItem, newItem: PostListItem): Boolean {
            return oldItem == newItem
        }
    }
) {

    // 클릭 인터페이스 정의
    interface MyItemClickListener {

        fun onItemClick(post: PostResult, position: Int, isReported: Boolean, reportText: String?)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = currentList.size

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): UnivTotalListAdapter.UnivTotalViewHolder {
        when (viewType) {
            0 -> {
                val binding: ItemPostBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(viewGroup.context),
                    R.layout.item_post,
                    viewGroup,
                    false
                )
                return UnivTotalViewHolder.ListItem(binding, mItemClickListener)
            }
            1 -> {
                val binding = ItemPostNextListLoadingBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                return UnivTotalViewHolder.Loading(binding)
            }
            2 -> {
                val binding = ItemPostNoMoreListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                return UnivTotalViewHolder.NoMorePost(binding)
            }
            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PostResult -> 0
            PostListItem.Loading -> 1
            PostListItem.NoMorePost -> 2
        }
    }

    override fun onBindViewHolder(holder: UnivTotalViewHolder, position: Int) {
        when (holder) {
            is UnivTotalViewHolder.ListItem -> holder.bind(getItem(position) as PostResult)
            is UnivTotalViewHolder.Loading -> {}
            is UnivTotalViewHolder.NoMorePost -> {}
        }
    }

    sealed class UnivTotalViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        class Loading(binding: ItemPostNextListLoadingBinding) : UnivTotalViewHolder(binding.root)
        class NoMorePost(binding: ItemPostNoMoreListBinding): UnivTotalViewHolder(binding.root)
        class ListItem(
            val binding: ItemPostBinding,
            private val itemClickListener: MyItemClickListener,
        ) : UnivTotalViewHolder(binding.root) {

            fun bind(postResult: PostResult) {
                binding.setVariable(BR.item, postResult)

//                if (postResult.blinded) {
//                    binding.likeimg.visibility = View.INVISIBLE
//                    binding.commentimg.visibility = View.INVISIBLE
//                    binding.photo.visibility = View.INVISIBLE
//                    binding.contenttext.visibility = View.INVISIBLE
//                    binding.titletext.visibility = View.INVISIBLE
//                    binding.liketext.visibility = View.INVISIBLE
//                    binding.commenttext.visibility = View.INVISIBLE
//                    binding.anonymous.visibility = View.INVISIBLE
//                    binding.time.visibility = View.INVISIBLE
//                    binding.dot.visibility = View.INVISIBLE
//
//                    binding.blindedText.visibility = View.VISIBLE
//                    binding.cancelBlindTv.visibility = View.VISIBLE
//
//                    binding.root.setOnClickListener {
//                        itemClickListener.onItemClick(
//                            post = postResult,
//                            position = absoluteAdapterPosition,
//                            isBlind = true,
//                            isReported = false,
//                            reportText = null,
//                        )
//                    }
//                } else
                if (postResult.reported) {
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
                    binding.blindedText.text = postResult.title

                    binding.root.setOnClickListener {
                        itemClickListener.onItemClick(
                            post = postResult,
                            position = absoluteAdapterPosition,
                            isReported = true,
                            reportText = postResult.title
                        )
                    }
                } else {
                    val fileattached = postResult.fileAttached
                    if (fileattached) {
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

                    binding.root.setOnClickListener {
                        itemClickListener.onItemClick(
                            post = postResult,
                            position = absoluteAdapterPosition,
                            isReported = false,
                            reportText = null
                        )
                    }
                }

                binding.executePendingBindings()

            }
        }
    }

    fun clearUnivTotalList() {
        this.submitList(emptyList())
    }

    fun addUnivTotalList(postList: List<PostListItem>, isFirst: Boolean) {
        if (!isFirst) {
            submitList(this.currentList.filter { it !is PostListItem.Loading } + postList)
        } else {
            submitList(postList)
        }
    }
}