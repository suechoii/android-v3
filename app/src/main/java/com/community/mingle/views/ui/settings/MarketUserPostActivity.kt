package com.community.mingle.views.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.community.mingle.R
import com.community.mingle.databinding.ActivityUserPostBinding
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.MarketPostViewModel
import com.community.mingle.viewmodel.SearchViewModel
import com.community.mingle.views.adapter.MarketMyPageVPAdapter
import com.community.mingle.views.adapter.ViewPagerAdapter
import com.community.mingle.views.ui.search.SearchVPAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class MarketUserPostActivity : BaseActivity<ActivityUserPostBinding>(R.layout.activity_user_post) {

    private lateinit var option: String

    private val information = arrayListOf("판매중","예약중","판매완료")

    private val viewModel: MarketPostViewModel by viewModels()

    private lateinit var viewPagerAdapter: MarketMyPageVPAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processIntent()
        initToolbar()
        initViewPager()

    }


    private fun processIntent() {
        option = intent.getStringExtra("option").toString()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.mypageToolbar)
        supportActionBar?.apply {
            binding.mypageTitleTv.text = option
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 생성
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    private fun initViewPager() {
        viewPagerAdapter = MarketMyPageVPAdapter(this)

        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabMypageCategory, binding.viewPager) { tab, position ->
            tab.text = information[position]
        }.attach()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return false
    }

}
