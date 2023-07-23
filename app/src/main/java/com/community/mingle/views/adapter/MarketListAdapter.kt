package com.community.mingle.views.adapter

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.community.mingle.BR
import com.community.mingle.R
import com.community.mingle.databinding.ItemMarketDefaultBinding
import com.community.mingle.service.models.market.MarketPostResult


class MarketListAdapter : RecyclerView.Adapter<MarketListAdapter.MarketViewHolder>() {
    private var marketList = ArrayList<MarketPostResult>()

    // 클릭 인터페이스 정의
    interface MyItemClickListener{
        fun onItemClick(post: MarketPostResult, position: Int)
        fun onHeartClick(post: MarketPostResult, position: Int)
        fun onFilledHeartClick(post: MarketPostResult, position: Int) //unlike
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = marketList.size

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): MarketListAdapter.MarketViewHolder {
        val binding : ItemMarketDefaultBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_market_default,
            viewGroup,
            false)
        return MarketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bind(marketList[position], position)
    }

    inner class MarketViewHolder(val binding : ItemMarketDefaultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(marketPostResult: MarketPostResult, position: Int) {
            binding.setVariable(BR.item, marketPostResult)

            if (marketPostResult.status != "판매중") {
                val overlayAlreadyApplied = binding.mainCard.childCount > 1
                Log.d("isOverlayAlreadyApplied", overlayAlreadyApplied.toString())

                val overlayColor = ContextCompat.getColor(binding.root.context, R.color.overlay_color)
                val overlayTextView = TextView(binding.root.context).apply {
                    setTextColor(Color.WHITE)
                    setBackgroundColor(overlayColor)
                    textSize = 16f
                    gravity = Gravity.CENTER
                }

                if (marketPostResult.status == "예약중") {
                    overlayTextView.text = "예약중"
                } else {
                    overlayTextView.text = "판매완료"
                }

                // Iterate through the child views and remove overlay views
                for (i in binding.mainCard.childCount - 1 downTo 1) {
                    val childView = binding.mainCard.getChildAt(i)
                    if (childView is TextView) {
                        binding.mainCard.removeViewAt(i)
                    }
                }

                binding.mainCard.addView(
                    overlayTextView,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            } else {
                // Remove any existing overlays
                for (i in binding.mainCard.childCount - 1 downTo 1) {
                    val childView = binding.mainCard.getChildAt(i)
                    if (childView is TextView) {
                        binding.mainCard.removeViewAt(i)
                    }
                }
            }

            if (marketPostResult.liked) {
                binding.favBtn.visibility = View.GONE
                binding.favFilledBtn.visibility = View.VISIBLE
            } else {
                binding.favBtn.visibility = View.VISIBLE
                binding.favFilledBtn.visibility = View.GONE
            }

            // Load the image into the ImageView using Glide
            Glide.with(binding.root)
                .load(marketPostResult.imgThumbnailUrl)
                .into(binding.itemImage)

            binding.root.setOnClickListener {
                mItemClickListener.onItemClick(marketPostResult, position)
            }

            binding.favBtn.setOnClickListener {
                mItemClickListener.onHeartClick(marketPostResult, position)
            }

            binding.favFilledBtn.setOnClickListener {
                mItemClickListener.onFilledHeartClick(marketPostResult, position)
            }

            binding.executePendingBindings()

        }
    }

    fun clearMarketList() {
        this.marketList.clear()
        notifyDataSetChanged()
    }

    fun addMarketList(postMarketList: List<MarketPostResult>, isFirst: Boolean) {
        if (isFirst) {
            this.marketList.clear()
        }
        this.marketList.addAll(postMarketList)
        notifyDataSetChanged()
    }

}