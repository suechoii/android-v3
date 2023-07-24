package com.community.mingle.views.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.ActivitySearchMarketBinding
import com.community.mingle.service.models.market.MarketPostResult
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.MarketPostViewModel
import com.community.mingle.views.ui.market.MarketPostActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class MarketSearchActivity : BaseActivity<ActivitySearchMarketBinding>(R.layout.activity_search_market) {

    private val viewModel: MarketPostViewModel by viewModels()
    private lateinit var searchListAdapter: MarketSearchListAdapter
    private var isFirst: Boolean = true
    private var clickedPosition: Int? = 0

    private var searchingQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MingleApplication.pref.isUpdate = false

        initView()
        initViewModel()
        initRV()

        binding.searchEt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                val query = s.toString()

                if (query == searchingQuery)
                    return

                searchingQuery = query

                MingleApplication.pref.keyword = query

                val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
                coroutineScope.launch {
                    delay(500) // debounce timeOut
                    if (query != searchingQuery)
                        return@launch

                    if (binding.searchEt.text.toString() != "") {
                        binding.beginSearchTv.text = ""
                        viewModel.getMarketSearchList(query, false)
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        if (MingleApplication.pref.isUpdate) {
            Log.d("hi","lol")
            if (searchingQuery != null) {
                viewModel.getMarketSearchList(searchingQuery!!, false)
            }
        }

    }

    private fun initView() {
        binding.returnIv.setOnClickListener {
            finish()
        }
        binding.removeTextIv.setOnClickListener {
            binding.searchEt.text = null
        }
    }

    private fun initViewModel() {
        binding.viewModel = viewModel

        viewModel.loading.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    binding.layoutProgress.root.visibility = View.VISIBLE
                } else {
                    binding.layoutProgress.root.visibility = View.GONE
                }
            }
        }

        viewModel.searchMarketList.observe(binding.lifecycleOwner!!) {
            if (it.isNullOrEmpty()) {
                searchListAdapter.clearMarketList()
                // 검색 결과가 없습니다.
                binding.beginSearchTv.text = "검색 결과가 없습니다"
            }
            else binding.beginSearchTv.text = ""
            searchListAdapter.addMarketList(it, isFirst)
        }

        viewModel.newMarketList.observe(binding.lifecycleOwner!!) {
            searchListAdapter.clearMarketList()
            binding.beginSearchTv.text = "검색어를 입력하세요"
        }
    }

    private fun initRV() {
        searchListAdapter = MarketSearchListAdapter()

        binding.defaultMarketRv.apply {
            adapter = searchListAdapter
            layoutManager = LinearLayoutManager(context)
            hasFixedSize()
        }

        searchListAdapter.setMyItemClickListener(object :
            MarketSearchListAdapter.MyItemClickListener {
            override fun onItemClick(post: MarketPostResult, position: Int) {
                clickedPosition = position
                val intent = Intent(this@MarketSearchActivity, MarketPostActivity::class.java)
                intent.putExtra("itemId", post.id)
                startActivity(intent)
            }

        })


    }

}