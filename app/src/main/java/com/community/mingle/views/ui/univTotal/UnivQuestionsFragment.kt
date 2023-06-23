package com.community.mingle.views.ui.univTotal

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.FragmentUnivtotalPageBinding
import com.community.mingle.service.models.PostResult
import com.community.mingle.utils.base.BaseFragment
import com.community.mingle.viewmodel.PostViewModel
import com.community.mingle.viewmodel.UnivTotalListViewModel
import com.community.mingle.views.adapter.UnivTotalListAdapter
import com.community.mingle.views.ui.board.PostActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnivQuestionsFragment : BaseFragment<FragmentUnivtotalPageBinding>(R.layout.fragment_univtotal_page) {
    private val viewModel: UnivTotalListViewModel by viewModels()
    private val viewModel2 : PostViewModel by viewModels()
    private lateinit var univListAdapter: UnivTotalListAdapter
    private lateinit var currentPostList: Array<PostResult>
    private var lastPostId: Int = 0
    private var tempLastPostId: Int = 0
    private var firstPosition: Int = 0
    private var clickedPosition: Int? = 0
    private var isFirst: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MingleApplication.pref.isUpdate = false

        initViewModel()
        initRV()
    }

    override fun onResume() {
        super.onResume()

        if (MingleApplication.pref.isUpdate) {
            if (firstPosition != 0) {
                viewModel.getUnivNextPosts(2, tempLastPostId)
            } else {
                isFirst = true
                viewModel.getUnivList(2, true)
            }
        }
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
        viewModel.getUnivList(2, false)

        viewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                   // binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    //binding.layoutProgress.root.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }

        viewModel2.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    binding.layoutProgress.root.visibility = View.GONE
                }
            }
        }

        viewModel.univTotalList2.observe(binding.lifecycleOwner!!) {
            if (it == null) {
                univListAdapter.clearUnivTotalList()
            }
            univListAdapter.addUnivTotalList(it, isFirst)
            binding.swipeRefresh.isRefreshing = false
            isFirst = false
            currentPostList = it.toTypedArray()
        }

        viewModel.clearUnivTotalList.observe(binding.lifecycleOwner!!) {
            univListAdapter.clearUnivTotalList()
        }

        viewModel.lastPostId2.observe(binding.lifecycleOwner!!) {
            lastPostId = it
            if (lastPostId != -1)
                tempLastPostId = lastPostId
        }

        viewModel.newUnivTotalList2.observe(binding.lifecycleOwner!!) {
            univListAdapter.addUnivTotalList(it, isFirst)
            binding.swipeRefresh.isRefreshing = false
            currentPostList = it.toTypedArray()
        }

        viewModel2.isUnblindPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    if (firstPosition != 0) {
                        viewModel.getUnivNextPosts(2, tempLastPostId)
                    } else {
                        isFirst = true
                        viewModel.getUnivList(2, true)
                    }
                }
            }
        }

    }

    private fun initRV() {
        univListAdapter = UnivTotalListAdapter()

        binding.univtotalRv.apply {
            adapter = univListAdapter
            layoutManager = LinearLayoutManager(context)
            hasFixedSize()
        }

        univListAdapter.setMyItemClickListener(object :
            UnivTotalListAdapter.MyItemClickListener {

            override fun onItemClick(post: PostResult, position: Int, isBlind: Boolean, isReported: Boolean, reportText: String?) {
                clickedPosition = position

                val intent = Intent(activity, PostActivity::class.java)
                intent.putExtra("postId", post.postId)
                intent.putExtra("type","잔디밭")
                intent.putExtra("isBlind",isBlind)
                intent.putExtra("isReported",isReported)
                intent.putExtra("reportText",reportText)

                startActivity(intent)
            }

            override fun onCancelClick(post: PostResult, position: Int) {
                clickedPosition = position
                viewModel2.unblindPost("잔디밭",post.postId)
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            isFirst = true
            viewModel.getUnivList(2, true)
        }

        binding.univtotalRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val totalCount = recyclerView.adapter!!.itemCount - 1
                firstPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()


                // 스크롤이 끝에 도달하면
                if (!binding.univtotalRv.canScrollVertically(1) && lastPosition == totalCount && lastPostId != -1) {
                    viewModel.getUnivNextPosts(2, lastPostId)
                }
            }
        })
    }
}