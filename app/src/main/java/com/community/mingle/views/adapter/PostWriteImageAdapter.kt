package com.community.mingle.views.adapter

import android.graphics.Bitmap
import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.R
import com.community.mingle.databinding.ItemPostWriteImageBinding
import java.net.URL

class PostWriteImageAdapter :
    RecyclerView.Adapter<PostWriteImageAdapter.PostWriteImageViewHolder>() {

    private var imageList = ArrayList<Bitmap>()
    private var urlList = ArrayList<URL>()
    private var images = ArrayList<Image>()

    override fun getItemCount(): Int = imageList.size

    interface OnPostWriteImageClickListener {
        fun onClickBtDelete(position: Int)
    }

    private lateinit var postWriteImageClickListener: OnPostWriteImageClickListener

    fun setOnPostWriteImageClickListener(listener: OnPostWriteImageClickListener) {
        this.postWriteImageClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostWriteImageAdapter.PostWriteImageViewHolder {
        val binding: ItemPostWriteImageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_post_write_image,
            parent,
            false
        )

        return PostWriteImageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PostWriteImageAdapter.PostWriteImageViewHolder,
        position: Int
    ) {
        holder.bind(imageList[position], position)
    }

    inner class PostWriteImageViewHolder(
        private val binding: ItemPostWriteImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bitmap: Bitmap, position: Int) {
            //binding.setVariable(BR.item, uri))
            //images.add()
            binding.ivImage.apply {
                setImageBitmap(bitmap)
                clipToOutline = true
            }
            binding.executePendingBindings()
            binding.btDelete.setOnClickListener {
                postWriteImageClickListener.onClickBtDelete(position)
            }
        }
    }

    fun clearItems() {
        this.imageList.clear()
        notifyDataSetChanged()
    }

    fun addUrls(url: URL) {
        // this.imageList.clear()
        this.urlList.add(url)
        notifyDataSetChanged()
    }

    fun addItem(bitmap: Bitmap) {
        // this.imageList.clear()
        this.imageList.add(bitmap)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        imageList.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItems(items: List<Bitmap>) {
        this.imageList.addAll(items)
        notifyDataSetChanged()
    }

    fun getItems(): List<Bitmap> {
        return this.imageList
    }

    fun updateItem(bitmap: Bitmap, position: Int) {
        imageList[position] = bitmap
        notifyItemChanged(position)
    }

}