package com.community.mingle.views.ui.search

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
import com.community.mingle.databinding.ItemMarketSearchBinding
import com.community.mingle.service.models.MarketPostResult


class MarketSearchListAdapter : RecyclerView.Adapter<MarketSearchListAdapter.MarketViewHolder>() {
    private var marketList = ArrayList<MarketPostResult>()

    // 클릭 인터페이스 정의
    interface MyItemClickListener{
        fun onItemClick(post: MarketPostResult, position: Int)
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
    ): MarketSearchListAdapter.MarketViewHolder {
        val binding : ItemMarketSearchBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_market_search,
            viewGroup,
            false)
        return MarketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bind(marketList[position], position)
    }

    inner class MarketViewHolder(val binding : ItemMarketSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(marketPostResult: MarketPostResult, position: Int) {
            binding.setVariable(BR.item, marketPostResult)

            Glide.with(binding.root).load(marketPostResult.imgThumbnailUrl).into(binding.itemImage)

            if (marketPostResult.status != "판매중") {
                // check if overlay has already been applied - notifyItemChanged로 바꾸면 필요
                val overlayAlreadyApplied = binding.mainCard.childCount > 1
                Log.d("isOverviewAlrApplied",overlayAlreadyApplied.toString())

                val overlayColor =
                    ContextCompat.getColor(binding.root.context, R.color.overlay_color)
                val overlayTextView = TextView(binding.root.context)
                if (overlayAlreadyApplied) {
                    overlayTextView.apply {
                        setTextColor(Color.WHITE)
                        textSize = 16f
                        gravity = Gravity.CENTER
                    }
                }
                else {
                    overlayTextView.apply {
                        setTextColor(Color.WHITE)
                        setBackgroundColor(overlayColor)
                        textSize = 16f
                        gravity = Gravity.CENTER
                    }
                }

                if (marketPostResult.status == "예약중") {
                    overlayTextView.text = "예약중"
                } else {
                    overlayTextView.text = "판매완료"
                }

                // Add the overlay to the cardview
                binding.mainCard.addView(
                    overlayTextView,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }

            binding.root.setOnClickListener {
                mItemClickListener.onItemClick(marketPostResult, position)
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