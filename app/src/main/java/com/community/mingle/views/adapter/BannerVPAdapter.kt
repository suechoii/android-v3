package com.community.mingle.views.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.community.mingle.R
import com.community.mingle.service.models.Banner
import com.community.mingle.service.models.HomeResult
import com.community.mingle.service.models.PostResult
import java.net.URL
class BannerVPAdapter() :

    RecyclerView.Adapter<BannerVPAdapter.MyViewHolder>(){
        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            private var photo: URL? = null
            private var itemimage: ImageView = itemView.findViewById<ImageView>(R.id.iv_images)

            fun onBind(image: Banner){
                //imageview에 목록에 해당되는 이미지를 출력
                this.photo = photo
                Glide.with(itemView.context).load(image.url).into(itemimage)
                itemView.setOnClickListener {
                    mItemClickListener.onItemClick(image)
                }
            }
        }

        // 클릭 인터페이스 정의
        interface MyItemClickListener{
            fun onItemClick(banner: Banner)
        }

        // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
        private lateinit var mItemClickListener: MyItemClickListener

        fun setMyItemClickListener(itemClickListener: MyItemClickListener){
            mItemClickListener = itemClickListener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerVPAdapter.MyViewHolder{
            val cardView = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
            return MyViewHolder(cardView)
        }

        private var bannerItemList: List<Banner>? = null

        override fun onBindViewHolder(holder: BannerVPAdapter.MyViewHolder, position: Int) {
            bannerItemList?.get(position)?.let {
                holder.onBind(it)
            }
        }

        fun submitlist(list: List<Banner>?) {
            bannerItemList = list
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            if (bannerItemList != null) {
                return bannerItemList!!.size
            } else return 0
        }

    }