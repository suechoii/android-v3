package com.community.mingle.views.ui.univTotal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TotalAllFragment : BaseFragment<FragmentUnivtotalPageBinding>(R.layout.fragment_univtotal_page) {
    private val viewModel: UnivTotalListViewModel by viewModels()
    private val viewModel2 : PostViewModel by viewModels()
    private lateinit var totalListAdapter: UnivTotalListAdapter
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
        Log.d("is it onResume", MingleApplication.pref.isUpdate.toString())
        super.onResume()

        if (MingleApplication.pref.isUpdate) {
            if (firstPosition != 0) {
                viewModel.loadNextAllTotalPosts()
            } else {
                isFirst = true
                viewModel.loadNewAllTotalPosts()
            }
        }

    }

    private fun initViewModel() {
        binding.viewModel = viewModel
        viewModel.loadNewAllTotalPosts()

        viewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (!it) {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }

        viewModel2.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    binding.swipeRefresh.isRefreshing = false
                    binding.layoutProgress.root.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalAllList.collect {
                    totalListAdapter.submitList(it)
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }

        viewModel2.isUnblindPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    if (firstPosition != 0) {
                        viewModel.loadNextAllTotalPosts()
                    } else {
                        isFirst = true
                        viewModel.loadNewAllTotalPosts()
                    }
                }
            }
        }

    }

    private fun initRV() {
        totalListAdapter = UnivTotalListAdapter()

        binding.univtotalRv.apply {
            adapter = totalListAdapter
            layoutManager = LinearLayoutManager(context)
            hasFixedSize()
        }

        totalListAdapter.setMyItemClickListener(object :
            UnivTotalListAdapter.MyItemClickListener {

            override fun onItemClick(post: PostResult, position: Int,isBlind: Boolean, isReported: Boolean, reportText: String?) {
                clickedPosition = position

                val intent = Intent(activity, PostActivity::class.java)
                intent.putExtra("postId", post.postId)
                intent.putExtra("type","잔디밭")
                intent.putExtra("board","학생회")
                intent.putExtra("tabName", "학생회게시판")
                intent.putExtra("isBlind",isBlind)
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
            viewModel.loadNewAllTotalPosts()
        }

        binding.univtotalRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val totalCount = recyclerView.adapter!!.itemCount - 1
                firstPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()

                // 스크롤이 끝에 도달하면
                if (!binding.univtotalRv.canScrollVertically(1) && lastPosition == totalCount && viewModel.totalAllList.value.isNotEmpty()) {
                    viewModel.loadNextAllTotalPosts()
                }
            }
        })
    }
}