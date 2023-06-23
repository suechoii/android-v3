package com.community.mingle.views.adapter

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.community.mingle.R
import com.community.mingle.service.models.Banner
import com.community.mingle.service.models.HomeResult
import com.community.mingle.service.models.PostResult
import java.net.URL
class MarketImageVPAdapter() :

    RecyclerView.Adapter<MarketImageVPAdapter.MyViewHolder>(){
        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            private var photo: URL? = null
            private var itemimage: ImageView = itemView.findViewById<ImageView>(R.id.iv_market_images)
            private var cv: CardView = itemView.findViewById<CardView>(R.id.cv_images)

            fun onBind(url: URL, status: String?, position: Int){
                //imageview에 목록에 해당되는 이미지를 출력
                this.photo = photo
                Glide.with(itemView.context).load(url).into(itemimage)

                val overlayAlreadyApplied = cv.childCount > 1

                if (overlayAlreadyApplied) {
                    cv.removeViewAt(1)
                }

                if (status != "SELLING") {
                    // Apply the overlay as before
                    val overlayColor = ContextCompat.getColor(itemView.context, R.color.overlay_color)
                    val overlayTextView = TextView(itemView.context)

                    overlayTextView.apply {
                        setTextColor(Color.WHITE)
                        setBackgroundColor(overlayColor)
                        textSize = 16f
                        gravity = Gravity.CENTER
                    }

                    if (status == "RESERVED") {
                        overlayTextView.text = "예약중"
                    } else {
                        overlayTextView.text = "판매완료"
                    }

                    cv.addView(
                        overlayTextView,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }

                itemView.setOnClickListener {
                    mItemClickListener.onItemClick(url, position)
                }
            }
        }

        // 클릭 인터페이스 정의
        interface MyItemClickListener{
            fun onItemClick(url : URL, position: Int)
        }

        // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
        private lateinit var mItemClickListener: MyItemClickListener

        lateinit var status: String

        fun setMyItemClickListener(itemClickListener: MyItemClickListener){
            mItemClickListener = itemClickListener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketImageVPAdapter.MyViewHolder{
            val cardView = LayoutInflater.from(parent.context).inflate(R.layout.item_market_card, parent, false)
            return MyViewHolder(cardView)
        }

        private var bannerItemList: List<URL>? = null

        override fun onBindViewHolder(holder: MarketImageVPAdapter.MyViewHolder, position: Int) {
            bannerItemList?.get(position)?.let {
                holder.onBind(it,status, position)
            }
        }

        fun submitlist(list: List<URL>?) {
            bannerItemList = list
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            if (bannerItemList != null) {
                return bannerItemList!!.size
            } else return 0
        }

    }
