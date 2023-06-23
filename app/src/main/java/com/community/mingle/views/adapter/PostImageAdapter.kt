package com.community.mingle.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.community.mingle.R
import java.net.URL

class PostImageAdapter :
    RecyclerView.Adapter<PostImageAdapter.PostImageViewHolder>() {

    private var imageList = ArrayList<URL>()

    inner class PostImageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var photo: URL? = null
        private var itemimage: ImageView = v.findViewById<ImageView>(R.id.itemImage)

        fun bindPhoto(photo: URL, position: Int) {
            this.photo = photo
            Glide.with(view.context).load(photo).into(itemimage)

            itemView.setOnClickListener {
                postImageClickListener.onClickPostImage(photo, position)}
        }
    }

    interface OnPostImageClickListener {
        fun onClickPostImage(item: URL, position: Int)
    }

    private lateinit var postImageClickListener: OnPostImageClickListener

    fun setOnPostImageClickListener(listener: PostImageAdapter.OnPostImageClickListener) {
        this.postImageClickListener = listener
    }

    override fun getItemCount(): Int = imageList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageAdapter.PostImageViewHolder {
        val inflatedView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_post_photo, parent, false)
        return PostImageViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: PostImageAdapter.PostImageViewHolder, position: Int) {
        val itemPhoto = imageList[position]
        holder.bindPhoto(itemPhoto, position)
    }

    fun addPostImageList(imageList: List<URL>) {
        this.imageList.clear()
        this.imageList.addAll(imageList)
        notifyDataSetChanged()
    }

}