package com.community.mingle.views.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.R
import com.community.mingle.databinding.ActivityMarketLikedBinding
import com.community.mingle.databinding.ActivityTermsBinding
import com.community.mingle.service.models.MarketPostResult
import com.community.mingle.utils.UserPostType.PRIVACY_TERMS
import com.community.mingle.utils.UserPostType.SERVICE_TERMS
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.MarketPostViewModel
import com.community.mingle.viewmodel.MyPageViewModel
import com.community.mingle.views.adapter.MarketListAdapter
import com.community.mingle.views.adapter.MarketMyPageListAdapter
import com.community.mingle.views.ui.LoadingDialog
import com.community.mingle.views.ui.market.MarketPostActivity
import com.community.mingle.views.ui.member.SignupActivity
import com.community.mingle.views.ui.member.TermsDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MarketLikedActivity : BaseActivity<ActivityMarketLikedBinding>(R.layout.activity_market_liked)  {

    private val viewModel: MarketPostViewModel by viewModels()
    private lateinit var marketListAdapter: MarketListAdapter
    private lateinit var currentMarketPostList: Array<MarketPostResult>
    private lateinit var option: String
    private lateinit var loadingDialog: LoadingDialog
    private var isFirst: Boolean = true
    private var lastPostId: Int = 0
    private var tempLastPostId: Int = 0
    private var firstPosition: Int = 0
    private var clickedPosition: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processIntent()
        initToolbar()
        initRV()
        initView()
        initViewModel()
    }

    private fun processIntent() {
        option = intent.getStringExtra("option").toString()
    }

    private fun initView() {
        loadingDialog = LoadingDialog(this@MarketLikedActivity)
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.marketLikedToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
        viewModel.getMarketLikedItemList(false)

        viewModel.marketMyPageList.observe(binding.lifecycleOwner!!) {
            if (it == null) {
                marketListAdapter.clearMarketList()
            }
            marketListAdapter.addMarketList(it, isFirst)
            isFirst = false
            currentMarketPostList = it.toTypedArray()
        }

        viewModel.lastMarketMyPagePostId.observe(binding.lifecycleOwner!!) {
            lastPostId = it
            if (lastPostId != -1)
                tempLastPostId = lastPostId
        }

        viewModel.newMarketList.observe(binding.lifecycleOwner!!) {
            marketListAdapter.addMarketList(it, isFirst)
            currentMarketPostList = it.toTypedArray()
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
                val intent = Intent(this@MarketLikedActivity, MarketPostActivity::class.java)
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
                marketListAdapter.notifyItemChanged(position)
            }

        })

        //refresh?


        binding.defaultMarketRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val totalCount = recyclerView.adapter!!.itemCount - 1
                firstPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
                // 스크롤이 끝에 도달하면
                if (!binding.defaultMarketRv.canScrollVertically(1) && lastPosition == totalCount && lastPostId != -1) {
                    viewModel.getMarketLikedItemNextPosts(lastPostId)
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return false
    }


}