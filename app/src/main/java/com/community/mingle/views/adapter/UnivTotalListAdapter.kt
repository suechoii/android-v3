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
import com.community.mingle.service.models.PostResult

class UnivTotalListAdapter : ListAdapter<PostResult, UnivTotalListAdapter.UnivTotalViewHolder>(
    object : DiffUtil.ItemCallback<PostResult>() {
        override fun areItemsTheSame(oldItem: PostResult, newItem: PostResult): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: PostResult, newItem: PostResult): Boolean {
            return oldItem == newItem
        }
    }
) {

    private var univTotalList = ArrayList<PostResult>()

    // 클릭 인터페이스 정의
    interface MyItemClickListener {

        fun onItemClick(post: PostResult, position: Int, isBlind: Boolean, isReported: Boolean, reportText: String?)
        fun onCancelClick(post: PostResult, position: Int)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = univTotalList.size

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): UnivTotalListAdapter.UnivTotalViewHolder {
        val binding: ItemPostBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_post,
            viewGroup,
            false
        )
        return UnivTotalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UnivTotalViewHolder, position: Int) {
        holder.bind(univTotalList[position], position)
    }

    inner class UnivTotalViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(postResult: PostResult, position: Int) {
            binding.setVariable(BR.item, postResult)

            if (postResult.blinded) {
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

                binding.root.setOnClickListener {
                    mItemClickListener.onItemClick(postResult, position, true, false, null)
                }
            } else if (postResult.reported) {
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
                    mItemClickListener.onItemClick(postResult, position, false, true, postResult.title)
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
                    mItemClickListener.onItemClick(postResult, position, false, false, null)
                }
            }

            binding.cancelBlindTv.setOnClickListener {
                mItemClickListener.onCancelClick(postResult, position)
            }

            binding.executePendingBindings()

        }
    }
//
//    fun clearUnivTotalList() {
//        this.univTotalList.clear()
//        notifyDataSetChanged()
//    }
//
//    fun addUnivTotalList(postList: List<PostResult>, isFirst: Boolean) {
//        if (isFirst) {
//            this.univTotalList.clear()
//        }
//        this.univTotalList.addAll(postList)
//        notifyDataSetChanged()
//    }
}