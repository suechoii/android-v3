package com.community.mingle.views.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.databinding.ActivitySearchBinding
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.MyPageViewModel
import com.community.mingle.viewmodel.PostViewModel
import com.community.mingle.viewmodel.SearchViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>(R.layout.activity_search) {

    private val viewModel: SearchViewModel by viewModels()

    private val information = arrayListOf("잔디밭","광장")
    private lateinit var tabLayout: TabLayout

    private lateinit var searchVPAdapter: SearchVPAdapter

    private var searchingQuery: String? = null

    private val viewModel2 : PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MingleApplication.pref.isUpdate = false

        initView()
        initVP()

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
//                        if (binding.searchContentVp.currentItem == 0)
//                            viewModel.getUnivSearchList(query, false)
//                        else
//                            viewModel.getTotalSearchList(query, false)
                        viewModel.getUnivSearchList(query, false)
                        viewModel.getTotalSearchList(query, false)
                    }
                    else {
//                        if (binding.searchContentVp.currentItem == 0) {
//                            viewModel.isNewUnivList()
//                        }else {
//                            viewModel.isNewTotalList()
//                        }
                        viewModel.isNewUnivList()
                        viewModel.isNewTotalList()
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
                viewModel.getUnivSearchList(searchingQuery!!, false)
                viewModel.getTotalSearchList(searchingQuery!!, false)
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

//        viewModel2.isUnblindPost.observe(binding.lifecycleOwner!!) { event ->
//            event.getContentIfNotHandled()?.let {
//                if (it) {
//                    Log.d("hi","sue")
//                    if (searchingQuery != null) {
//                        viewModel.getUnivSearchList(searchingQuery!!, false)
//                        viewModel.getTotalSearchList(searchingQuery!!, false)
//                    }
//                }
//            }
//        }
    }

    private fun initVP() {
        searchVPAdapter = SearchVPAdapter(this)

        binding.searchContentVp.adapter = searchVPAdapter

        tabLayout = binding.tabSearchCategory

        TabLayoutMediator(binding.tabSearchCategory, binding.searchContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

    }

}