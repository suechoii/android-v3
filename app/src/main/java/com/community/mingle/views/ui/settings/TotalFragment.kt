package com.community.mingle.views.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.FragmentUnivtotalMypageBinding
import com.community.mingle.service.models.PostResult
import com.community.mingle.utils.UserPostType.MY_COMMENT_POST
import com.community.mingle.utils.UserPostType.MY_LIKE_POST
import com.community.mingle.utils.UserPostType.MY_POST
import com.community.mingle.utils.UserPostType.MY_SCRAP_POST
import com.community.mingle.utils.base.BaseFragment
import com.community.mingle.viewmodel.MyPageViewModel
import com.community.mingle.viewmodel.PostViewModel
import com.community.mingle.views.adapter.UnivTotalListAdapter
import com.community.mingle.views.ui.board.PostActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TotalFragment(private val option: String) : BaseFragment<FragmentUnivtotalMypageBinding>(R.layout.fragment_univtotal_mypage) {

    private val viewModel: MyPageViewModel by viewModels()
    private val viewModel2 : PostViewModel by viewModels()
    private lateinit var postListAdapter: UnivTotalListAdapter
    private lateinit var currentPostList: Array<PostResult>
    private var lastPostId: Int = 0
    private var tempLastPostId: Int = 0
    private var firstPosition: Int = 0
    private var clickedPosition: Int? = 0
    private var isFirst: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MingleApplication.pref.isUpdate = false

        initRV()
        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        if (MingleApplication.pref.isUpdate) {
            when (option) {
                MY_POST -> {
                    isFirst = true
                    viewModel.getMyTotalPostList(10000000, true)
                }
                MY_COMMENT_POST -> {
                    isFirst = true
                    viewModel.getMyTotalCommentPostList(10000000,true)
                }
                MY_SCRAP_POST -> {
                    isFirst = true
                    viewModel.getMyTotalPostList(10000000,true)
                }
                MY_LIKE_POST -> {
                    isFirst = true
                    viewModel.getMyLikedTotalPostList(10000000,true)
                }
            }
        }
    }

    private fun initViewModel() {
        binding.viewModel = viewModel

        when (option) {
            MY_POST -> {
                viewModel.getMyTotalPostList(10000000, false)
            }
            MY_COMMENT_POST -> {
                viewModel.getMyTotalCommentPostList(10000000,false)
            }
            MY_SCRAP_POST -> {
                viewModel.getMyScrapTotalPostList(10000000,false)
            }
            MY_LIKE_POST -> {
                viewModel.getMyLikedTotalPostList(10000000,false)
            }
        }

//        viewModel.loading.observe(binding.lifecycleOwner!!) { event ->
//            event.getContentIfNotHandled()?.let {
//                if (it) {
//                    binding.layoutProgress.root.visibility = View.VISIBLE
//                } else {
//                    binding.layoutProgress.root.visibility = View.GONE
//                }
//            }
//        }

        viewModel2.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    binding.layoutProgress.root.visibility = View.GONE
                }
            }
        }

        viewModel.postList.observe(binding.lifecycleOwner!!) {
            if (it == null) {
                postListAdapter.clearUnivTotalList()
            }
            postListAdapter.addUnivTotalList(it, true)
            isFirst = false
            currentPostList = it.toTypedArray()
        }

        viewModel.clearUnivTotalList.observe(binding.lifecycleOwner!!) {
            postListAdapter.clearUnivTotalList()
        }

        viewModel.lastPostId.observe(binding.lifecycleOwner!!) {
            lastPostId = it
            if (lastPostId != -1)
                tempLastPostId = lastPostId
        }

        viewModel.newUnivTotalList.observe(binding.lifecycleOwner!!) {
            postListAdapter.addUnivTotalList(it, isFirst)
            currentPostList = it.toTypedArray()
        }

        viewModel2.isUnblindPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    when (option) {
                        MY_POST -> {
                            isFirst = true
                            viewModel.getMyTotalPostList(10000000, true)
                        }
                        MY_COMMENT_POST -> {
                            isFirst = true
                            viewModel.getMyTotalCommentPostList(10000000,true)
                        }
                        MY_SCRAP_POST -> {
                            isFirst = true
                            viewModel.getMyScrapTotalPostList(10000000,true)
                        }
                        MY_LIKE_POST -> {
                            isFirst = true
                            viewModel.getMyLikedTotalPostList(10000000,true)
                        }
                    }
                }
            }
        }

    }

    private fun initRV() {
        postListAdapter = UnivTotalListAdapter()

        binding.mypageUnivtotalRv.apply {
            adapter = postListAdapter
            layoutManager = LinearLayoutManager(context)
            hasFixedSize()
        }

        postListAdapter.setMyItemClickListener(object :
            UnivTotalListAdapter.MyItemClickListener {
            override fun onItemClick(item: PostResult, position: Int, isBlind: Boolean, isReported: Boolean, reportText: String?) {
                clickedPosition = position
                val intent = Intent(activity, PostActivity::class.java)
                intent.putExtra("postId", item.postId)
                intent.putExtra("type","광장")
                intent.putExtra("isBlind",isBlind)
                intent.putExtra("isReported",isReported)
                intent.putExtra("reportText",reportText)
                startActivity(intent)
            }

            override fun onCancelClick(post: PostResult, position: Int) {
                clickedPosition = position
                viewModel2.unblindPost("광장",post.postId)
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            isFirst = true
            when (option) {
                MY_POST -> {
                    viewModel.getMyTotalPostList(10000000, true)
                }
                MY_COMMENT_POST -> {
                    viewModel.getMyTotalCommentPostList(10000000,true)
                }
                MY_SCRAP_POST -> {
                    viewModel.getMyScrapTotalPostList(10000000,true)
                }
                MY_LIKE_POST -> {
                    viewModel.getMyLikedTotalPostList(10000000,true)
                }
            }
            binding.swipeRefresh.isRefreshing = false
        }

        binding.mypageUnivtotalRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val totalCount = recyclerView.adapter!!.itemCount - 1
                firstPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()


                // 스크롤이 끝에 도달하면
                if (!binding.mypageUnivtotalRv.canScrollVertically(1) && lastPosition == totalCount && lastPostId != -1) {
                    when (option) {
                        MY_POST -> {
                            viewModel.getMyTotalNextPosts(lastPostId)
                        }
                        MY_COMMENT_POST -> {
                            viewModel.getMyTotalNextCommentedPosts(lastPostId)
                        }
                        MY_SCRAP_POST -> {
                            viewModel.getMyTotalNextScrapedPosts(lastPostId)
                        }
                        MY_LIKE_POST -> {
                            viewModel.getMyTotalNextLikedPosts(lastPostId)
                        }
                    }
                }
            }
        })
    }



}