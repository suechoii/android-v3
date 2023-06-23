package com.community.mingle.views.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.FragmentSearchPageBinding
import com.community.mingle.service.models.PostResult
import com.community.mingle.utils.base.BaseFragment
import com.community.mingle.utils.base.BaseSearchFragment
import com.community.mingle.viewmodel.PostViewModel
import com.community.mingle.viewmodel.SearchViewModel
import com.community.mingle.views.adapter.UnivTotalListAdapter
import com.community.mingle.views.ui.board.PostActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchTotalFragment() : BaseSearchFragment<FragmentSearchPageBinding>(R.layout.fragment_search_page) {
    private lateinit var totalListAdapter: UnivTotalListAdapter
    private var clickedPosition: Int? = 0
    private var isFirst: Boolean = true

    private val viewModel2 : PostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.noSearchResultTv.text = "광장에 궁금한 것을 물어보세요"
        initViewModel()
        initRV()
    }

    override fun initViewModel() {
        binding.viewModel = searchViewModel

        searchViewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    binding.layoutProgress.root.visibility = View.GONE
                }
            }
        }

        searchViewModel.searchTotalList.observe(binding.lifecycleOwner!!) {
            if (it.isNullOrEmpty()) {
                totalListAdapter.clearUnivTotalList()
                binding.noSearchResultTv.text = "검색 결과가 없습니다"
            }
            else binding.noSearchResultTv.text = ""
            totalListAdapter.addUnivTotalList(it, isFirst)
        }

        searchViewModel.newTotalList.observe(binding.lifecycleOwner!!) {
            totalListAdapter.clearUnivTotalList()
            binding.noSearchResultTv.text = "광장에 궁금한 것을 물어보세요"
        }

        viewModel2.isUnblindPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    Log.d("hi","sue")
                    if (MingleApplication.pref.keyword != null) {
                        searchViewModel.getTotalSearchList(MingleApplication.pref.keyword!!, false)
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

            override fun onItemClick(post: PostResult, position: Int, isBlind: Boolean, isReported: Boolean, reportText: String?) {
                clickedPosition = position

                val intent = Intent(activity, PostActivity::class.java)
                intent.putExtra("postId", post.postId)
                intent.putExtra("type","광장")
                intent.putExtra("isBlind",isBlind)
                intent.putExtra("isReported",isReported)
                intent.putExtra("reportText",reportText)

                startActivity(intent)
            }

            override fun onCancelClick(post: PostResult, position: Int) {
                clickedPosition = position
                viewModel2.unblindPost("광장",post.postId)
                isFirst = true
            }
        })

    }
}