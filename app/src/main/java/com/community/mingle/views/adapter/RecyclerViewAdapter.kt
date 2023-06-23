package com.community.mingle.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.BR
import com.community.mingle.R
import com.community.mingle.databinding.ItemPostBinding
import com.community.mingle.service.models.PostResult

class RecyclerViewAdapter(val items: MutableList<PostResult?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 클릭 인터페이스 정의
    interface MyItemClickListener{
        fun onItemClick(post: PostResult, position: Int)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    companion object {
        private const val ITEM = 1
        private const val LOADING = 0
    }

    inner class ItemViewHolder(val binding : ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(postResult: PostResult, position: Int) {
            binding.setVariable(BR.item, postResult)

            val fileattached = postResult.fileAttached
            if (fileattached) {
                binding.photo.visibility = View.VISIBLE
            }else{
                binding.photo.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                mItemClickListener.onItemClick(postResult, position)
            }

            binding.executePendingBindings()
        }
    }

    inner class LoadingViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loadingBar = itemView.findViewById<ProgressBar>(R.id.loading_bar)
    }

    override fun getItemViewType(position: Int) : Int {
        return if (items[position] != null) {
            ITEM
        } else {
            LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM) {
            val binding : ItemPostBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_post,
                parent,
                false)
            ItemViewHolder(binding)
        } else {
            val view  =
                LayoutInflater.from(parent.context).inflate(
                R.layout.progress_dialog_layout,
                parent,
                false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bindItems(items[position]!!, position)
        } else {

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}