package com.community.mingle.views.ui.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.BottomDialogChangeStatusBinding
import com.community.mingle.databinding.FragmentMarketBinding
import com.community.mingle.databinding.FragmentMarketMypageBinding
import com.community.mingle.databinding.FragmentUnivtotalPageBinding
import com.community.mingle.service.models.MarketPostResult
import com.community.mingle.service.models.PostResult
import com.community.mingle.utils.Constants.toast
import com.community.mingle.utils.ResUtils
import com.community.mingle.utils.base.BaseFragment
import com.community.mingle.viewmodel.MarketPostViewModel
import com.community.mingle.viewmodel.PostViewModel
import com.community.mingle.viewmodel.UnivTotalListViewModel
import com.community.mingle.views.adapter.MarketListAdapter
import com.community.mingle.views.adapter.MarketMyPageListAdapter
import com.community.mingle.views.adapter.UnivTotalListAdapter
import com.community.mingle.views.ui.board.PostActivity
import com.community.mingle.views.ui.market.MarketPostActivity
import com.community.mingle.views.ui.settings.MyPageActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoldFragment : BaseFragment<FragmentMarketMypageBinding>(R.layout.fragment_market_mypage) {
    private val viewModel: MarketPostViewModel by viewModels()
    private lateinit var marketListAdapter: MarketMyPageListAdapter
    private var lastPostId: Int = 0
    private var tempLastPostId: Int = 0
    private var firstPosition: Int = 0
    private var clickedPosition: Int? = 0
    private var currentStatus: String = "SOLDOUT"

    //var isFirstClicked : Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MingleApplication.pref.isUpdate = false

        initViewModel()
        initRV()

        //isFirstClicked = false
    }

    private fun initViewModel() {
        binding.viewModel = viewModel

        viewModel.getMarketItemList(true, "SOLDOUT")

        viewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    binding.layoutProgress.root.visibility = View.GONE
                }
            }
        }

        viewModel.marketSoldoutList.observe(binding.lifecycleOwner!!) {
            marketListAdapter.submitList(it)
        }

        viewModel.clearMarketList.observe(binding.lifecycleOwner!!) {
            marketListAdapter.submitList(emptyList())
        }

        viewModel.lastMarketSoldoutPostId.observe(binding.lifecycleOwner!!) {
            lastPostId = it
            if (lastPostId != -1)
                tempLastPostId = lastPostId
        }

        viewModel.isChangeStatus.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    if (currentStatus != "SOLDOUT") {
                        marketListAdapter.submitList(emptyList())
                        viewModel.getMarketItemList(true,"SOLDOUT")
                        viewModel.getMarketItemList(true,currentStatus)
                    }
                }
            }
        }

    }

    private fun initRV() {
        marketListAdapter = MarketMyPageListAdapter()

        binding.mypageMarketRv.apply {
            adapter = marketListAdapter
            layoutManager = LinearLayoutManager(context)
            hasFixedSize()
        }

        marketListAdapter.setMyItemClickListener(object :
            MarketMyPageListAdapter.MyItemClickListener {

            override fun onItemClick(post: MarketPostResult, position: Int) {
                clickedPosition = position
                val intent = Intent(activity, MarketPostActivity::class.java)
                intent.putExtra("itemId", post.id)
                startActivity(intent)
            }

            override fun onChangeStatusClick(post: MarketPostResult, position: Int) {
                showChangeStatusDialog(post.id)
            }

        })

        binding.mypageMarketRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val totalCount = recyclerView.adapter!!.itemCount - 1
                firstPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
                // 스크롤이 끝에 도달하면
                if (!binding.mypageMarketRv.canScrollVertically(1) && lastPosition == totalCount && lastPostId != -1) {
                    viewModel.getMarketItemNextPosts(lastPostId,"SOLDOUT")
                }
            }
        })
    }

    private fun showChangeStatusDialog(itemId: Int) {
        val bottomDialogBinding = BottomDialogChangeStatusBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.DialogCustomTheme)
        dialog.behavior.state=  BottomSheetBehavior.STATE_EXPANDED
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(bottomDialogBinding.root)
        dialog.show()

        bottomDialogBinding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.status_one -> {
                    viewModel.changeStatus(itemId,"SELLING")
                    currentStatus = "SELLING"
                    dialog.dismiss()
                }
                R.id.status_two -> {
                    viewModel.changeStatus(itemId,"RESERVED")
                    currentStatus = "RESERVED"
                    dialog.dismiss()
                }
                R.id.status_three -> {
                    viewModel.changeStatus(itemId, "SOLDOUT")
                    dialog.dismiss()
                }
            }
            false
        }
    }
}