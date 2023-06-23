package com.community.mingle.views.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.community.mingle.R
import com.community.mingle.databinding.ViewPagerItemImageBinding
import java.net.URL


class ImageSlideAdapter() :
    RecyclerView.Adapter<ImageSlideAdapter.ImageSlideViewHolder>() {

    private var imageList = ArrayList<URL>()

    override fun getItemCount(): Int = imageList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageSlideAdapter.ImageSlideViewHolder {
        val binding: ViewPagerItemImageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.view_pager_item_image,
            parent,
            false
        )

        return ImageSlideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageSlideAdapter.ImageSlideViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    inner class ImageSlideViewHolder(
        private val binding: ViewPagerItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(postURL: URL) {
            Glide.with(binding.root.context)
                .load(postURL)
                .into(binding.ivImage)
        }
    }

    fun addPostImageList(imageList: List<URL>) {
        this.imageList.clear()
        this.imageList.addAll(imageList)
        notifyDataSetChanged()
    }
}