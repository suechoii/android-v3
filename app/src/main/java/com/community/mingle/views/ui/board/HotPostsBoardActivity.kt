package com.community.mingle.views.ui.board

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.R
import com.community.mingle.common.IntentConstants
import com.community.mingle.databinding.ActivityHotPostsBoardBinding
import com.community.mingle.service.models.PostResult
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.HotPostsBoardViewModel
import com.community.mingle.viewmodel.PostViewModel
import com.community.mingle.views.adapter.UnivTotalListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HotPostsBoardActivity : BaseActivity<ActivityHotPostsBoardBinding>(R.layout.activity_hot_posts_board) {

    private val hotPostViewModel: HotPostsBoardViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSwipeRefresh()
        initRecyclerView()
        initBackButton()
        setStateListener()
    }

    private fun initBackButton() {
        binding.backImageButton.setOnClickListener {
            finish()
        }
    }

    private fun setStateListener() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                hotPostViewModel.bestPostList.collect {
                    (binding.hotPostsRecyclerView.adapter as UnivTotalListAdapter).submitList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                hotPostViewModel.swipeLoading.collect {
                    if (!it) binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun initSwipeRefresh() {
        binding.swipeRefresh.apply {
            isRefreshing = false
            setOnRefreshListener { hotPostViewModel.swipeRefreshHotPostList() }
        }
    }

    private fun initRecyclerView() {
        binding.hotPostsRecyclerView.apply {
            adapter = UnivTotalListAdapter()
                .apply {
                    setMyItemClickListener(object : UnivTotalListAdapter.MyItemClickListener {
                        override fun onItemClick(post: PostResult, position: Int, isReported: Boolean, reportText: String?) {
                            val intent = Intent(context, PostActivity::class.java)
                            intent.putExtra("postId", post.postId)
                            intent.putExtra(IntentConstants.BoardType, post.boardType)
                            intent.putExtra(IntentConstants.CategoryType, post.categoryType)
                            //intent.putExtra("isBlind", isBlind)
                            intent.putExtra("isReported", isReported)
                            intent.putExtra("reportText", reportText)
                            startActivity(intent)
                        }
                    })
                }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                    val totalCount = recyclerView.adapter!!.itemCount - 1
                    if (!canScrollVertically(1) && lastPosition == totalCount && hotPostViewModel.lastPostId != -1) {
                        hotPostViewModel.loadNextHotPostList()
                    }
                }
            })
        }
    }
}