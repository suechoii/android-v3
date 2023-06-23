package com.community.mingle.views.ui.market

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
import com.community.mingle.databinding.FragmentMarketBinding
import com.community.mingle.databinding.FragmentUnivtotalPageBinding
import com.community.mingle.service.models.MarketPostResult
import com.community.mingle.service.models.PostResult
import com.community.mingle.utils.ResUtils
import com.community.mingle.utils.base.BaseFragment
import com.community.mingle.viewmodel.MarketPostViewModel
import com.community.mingle.viewmodel.PostViewModel
import com.community.mingle.viewmodel.UnivTotalListViewModel
import com.community.mingle.views.adapter.MarketListAdapter
import com.community.mingle.views.adapter.UnivTotalListAdapter
import com.community.mingle.views.ui.board.PostActivity
import com.community.mingle.views.ui.notification.NotiActivity
import com.community.mingle.views.ui.search.MarketSearchActivity
import com.community.mingle.views.ui.settings.MyPageActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MarketFragment : BaseFragment<FragmentMarketBinding>(R.layout.fragment_market) {
    private val viewModel: MarketPostViewModel by viewModels()
    private lateinit var marketListAdapter: MarketListAdapter
    private lateinit var currentMarketPostList: Array<MarketPostResult>
    private var lastPostId: Int = 0
    private var tempLastPostId: Int = 0
    private var firstPosition: Int = 0
    private var clickedPosition: Int? = 0
    private var isFirst: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MingleApplication.pref.isUpdate = false

        initView()
        initViewModel()
        initRV()
    }

    override fun onResume() {
//        Log.d("is it onResume",MingleApplication.pref.isUpdate.toString())
        super.onResume()
//
//        if (MingleApplication.pref.isUpdate) {
//            if (firstPosition != 0) {
//                Log.d("what to do","ah")
//                viewModel.getMarketNextPosts(tempLastPostId)
//            } else {
//                Log.d("what to do","ah2")
//                isFirst = true
//                viewModel.getMarketList( true)
//            }
//
//        }

    }

    private fun initView() {
        binding.mypage.setOnClickListener {
            val intent = Intent(activity, MyPageActivity::class.java)
            startActivity(intent)
        }

        binding.searchIv.setOnClickListener {
            val intent = Intent(activity, MarketSearchActivity::class.java)
            startActivity(intent)
        }

        binding.bell.setOnClickListener {
            val intent = Intent(activity, NotiActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
        viewModel.getMarketList( false)

        viewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    //binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    //binding.layoutProgress.root.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }

        viewModel.marketList.observe(binding.lifecycleOwner!!) {
            if (it == null) {
                marketListAdapter.clearMarketList()
            }
            marketListAdapter.addMarketList(it, isFirst)
            binding.swipeRefresh.isRefreshing = false
            isFirst = false
            currentMarketPostList = it.toTypedArray()
        }

        viewModel.clearMarketList.observe(binding.lifecycleOwner!!) {
            marketListAdapter.clearMarketList()
        }

        viewModel.lastMarketPostId.observe(binding.lifecycleOwner!!) {
            lastPostId = it
            if (lastPostId != -1)
                tempLastPostId = lastPostId
        }

        viewModel.newMarketList.observe(binding.lifecycleOwner!!) {
            marketListAdapter.addMarketList(it, isFirst)
            binding.swipeRefresh.isRefreshing = false
            currentMarketPostList = it.toTypedArray()
        }

        // like post
        viewModel.isLikedPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                 // increase number of likes by 1 , change heart colour
                   // clickedPosition?.let { it1 -> marketListAdapter.notifyItemChanged(it1) }
                    marketListAdapter.notifyDataSetChanged()
                }
            }
        }

        // unlike post
        viewModel.isUnlikePost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    marketListAdapter.notifyDataSetChanged()
                }
            }
        }

    }

    private fun initRV() {
        marketListAdapter = MarketListAdapter()

        binding.defaultMarketRv.apply {
            adapter = marketListAdapter
            layoutManager = LinearLayoutManager(context)
            hasFixedSize()
        }

        marketListAdapter.setMyItemClickListener(object :
            MarketListAdapter.MyItemClickListener {

            override fun onItemClick(post: MarketPostResult, position: Int) {
                clickedPosition = position
                val intent = Intent(activity, MarketPostActivity::class.java)
                intent.putExtra("itemId", post.id)
                startActivity(intent)
            }

            override fun onHeartClick(post: MarketPostResult, position: Int) {
                clickedPosition = position
                viewModel.likeMarketPost(post.id)
                post.likeCount = (post.likeCount.toInt() + 1).toString()
                post.liked = true
            }

            override fun onFilledHeartClick(post: MarketPostResult, position: Int) {
                clickedPosition = position
                viewModel.unlikeMarketPost(post.id)
                post.likeCount = (post.likeCount.toInt() - 1).toString()
                post.liked = false
//                marketListAdapter.notifyItemChanged(position)
            }

        })

        binding.swipeRefresh.setOnRefreshListener {
            isFirst = true
            viewModel.getMarketList( true)
        }

        binding.deal.setOnClickListener {
            val intent = Intent(activity, MarketPostWriteActivity::class.java)
            startActivity(intent)
        }

        binding.defaultMarketRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val totalCount = recyclerView.adapter!!.itemCount - 1
                firstPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
                // 스크롤이 끝에 도달하면
                if (!binding.defaultMarketRv.canScrollVertically(1) && lastPosition == totalCount && lastPostId != -1) {
                    viewModel.getMarketNextPosts(lastPostId)
                }
            }
        })
    }
}