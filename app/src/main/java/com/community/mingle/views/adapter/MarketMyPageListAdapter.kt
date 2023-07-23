package com.community.mingle.views.adapter

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.community.mingle.BR
import com.community.mingle.MainActivity
import com.community.mingle.R
import com.community.mingle.databinding.ItemMarketMypageBinding
import com.community.mingle.service.models.market.MarketPostResult
import com.community.mingle.utils.DialogUtils.showYesNoDialog
import com.community.mingle.viewmodel.MarketPostViewModel
import com.community.mingle.views.ui.market.MarketPostEditActivity

class MarketMyPageListAdapter : ListAdapter<MarketPostResult, MarketMyPageListAdapter.MarketViewHolder>(
    object : DiffUtil.ItemCallback<MarketPostResult>() {
        override fun areItemsTheSame(oldItem: MarketPostResult, newItem: MarketPostResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MarketPostResult, newItem: MarketPostResult): Boolean {
            return oldItem == newItem
        }

    }
) {

    // 클릭 인터페이스 정의
    interface MyItemClickListener {

        fun onItemClick(post: MarketPostResult, position: Int)
        fun onChangeStatusClick(post: MarketPostResult, position: Int)
    }

    // 리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
    ): MarketMyPageListAdapter.MarketViewHolder {
        val binding: ItemMarketMypageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_market_mypage,
            viewGroup,
            false
        )
        return MarketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bind(getItem(position), position)

    }

    inner class MarketViewHolder(val binding: ItemMarketMypageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(marketPostResult: MarketPostResult, position: Int) {
            binding.setVariable(BR.item, marketPostResult)

            Glide.with(binding.root).load(marketPostResult.imgThumbnailUrl).into(binding.itemImage)

            if (marketPostResult.status != "판매중") {
                // check if overlay has already been applied - notifyItemChanged로 바꾸면 필요
                val overlayAlreadyApplied = binding.mainCard.childCount > 1
                Log.d("isOverviewAlrApplied", overlayAlreadyApplied.toString())
                val overlayColor =
                    ContextCompat.getColor(binding.root.context, R.color.overlay_color)
                val overlayTextView = TextView(binding.root.context)
                if (overlayAlreadyApplied) {
                    overlayTextView.apply {
                        setTextColor(Color.WHITE)
                        textSize = 16f
                        gravity = Gravity.CENTER
                    }
                } else {
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

            binding.chgStatusTv.setOnClickListener {
                mItemClickListener.onChangeStatusClick(marketPostResult, position)
            }

            binding.moreBtn.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, binding.moreBtn)
                popupMenu.menuInflater.inflate(R.menu.market_mypage_menu, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    val b = when (menuItem.itemId) {
                        R.id.market_mypage_edit -> {
                            // Handle the "Edit" menu item click
                            // 수정 버튼 누를 시
                            val intent = Intent(itemView.context, MarketPostEditActivity::class.java).apply {
                                putExtra("itemId", marketPostResult.id)
                                putExtra("title", marketPostResult.title)
                                putExtra("content", marketPostResult.content)
                                putExtra("price", marketPostResult.price)
                                putExtra("chatUrl", marketPostResult.chatUrl)
                                putExtra("place", marketPostResult.location)
                                val urlStrings = marketPostResult.itemImgList.map { it.toString() }
                                putStringArrayListExtra("imageList", urlStrings as ArrayList<String>)
                                Log.d("checkUrlString", urlStrings.toString())
                            }

                            itemView.context.startActivity(intent)
                            true
                        }

                        R.id.market_mypage_delete -> {
                            // Handle the "Delete" menu item click
                            val viewModelProvider = ViewModelProvider(itemView.context as AppCompatActivity)
                            val viewModel = viewModelProvider.get(MarketPostViewModel::class.java)
                            showYesNoDialog(itemView.context, "거래글을 삭제하시겠습니까?", onPositiveClick = { dialog, which ->
                                viewModel.deleteMarketPost(marketPostResult.id)
                                // 우선은 다 홈으로 돌아가는걸로 설정, 나중에 잔디밭이나 광장에서 클릭한 경우라면 어떻게 해야할지도 생각
                                val intent = Intent(itemView.context, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                itemView.context.startActivity(intent)
                            },
                                onNegativeClick = { dialog, _ ->
                                })
                            true
                        }

                        else -> false
                    }
                    b
                }

                popupMenu.show()
            }

            binding.executePendingBindings()

        }
    }
}