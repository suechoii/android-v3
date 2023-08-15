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
import com.community.mingle.common.IntentConstants
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
class SearchUnivFragment() : BaseSearchFragment<FragmentSearchPageBinding>(R.layout.fragment_search_page) {
    private lateinit var univListAdapter: UnivTotalListAdapter
    private lateinit var currentPostList: Array<PostResult>
    private var clickedPosition: Int? = 0
    private var isFirst: Boolean = true
    private var firstPosition: Int = 0


    private val viewModel2 : PostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.noSearchResultTv.text = "잔디밭에 궁금한 것을 물어보세요"
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

        searchViewModel.searchUnivList.observe(binding.lifecycleOwner!!) {
            if (it.isNullOrEmpty()) {
                univListAdapter.clearUnivTotalList()
                // 검색 결과가 없습니다.
                binding.noSearchResultTv.text = "검색 결과가 없습니다"
            }
            else binding.noSearchResultTv.text = ""
            univListAdapter.addUnivTotalList(it, isFirst)
        }

        searchViewModel.newUnivList.observe(binding.lifecycleOwner!!) {
            univListAdapter.clearUnivTotalList()
            binding.noSearchResultTv.text = "잔디밭에 궁금한 것을 물어보세요"
        }

        viewModel2.isUnblindPost.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    Log.d("hi","sue")
                    if (MingleApplication.pref.keyword != null) {
                        searchViewModel.getUnivSearchList(MingleApplication.pref.keyword!!, false)
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

            override fun onItemClick(post: PostResult, position: Int, isReported: Boolean, reportText: String?) {
                clickedPosition = position

                val intent = Intent(activity, PostActivity::class.java)
                intent.putExtra("postId", post.postId)
                intent.putExtra(IntentConstants.BoardType,"잔디밭")
                intent.putExtra(IntentConstants.CategoryType, post.categoryType)
                //intent.putExtra("isBlind",isBlind)
                intent.putExtra("isReported",isReported)
                intent.putExtra("reportText",reportText)

                startActivity(intent)
            }
        })

    }
}