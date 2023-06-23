package com.community.mingle.views.ui.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.community.mingle.R
import com.community.mingle.databinding.ActivityUserPostBinding
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.MyPageViewModel
import com.community.mingle.views.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserPostActivity : BaseActivity<ActivityUserPostBinding>(R.layout.activity_user_post) {

    private lateinit var option: String

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
        val fragmentList: ArrayList<Fragment> = arrayListOf(
            UnivFragment(option),
            TotalFragment(option)
        )

        val titleList: ArrayList<String> = arrayListOf(
            "잔디밭",
            "광장"
        )

        val viewPagerAdapter = ViewPagerAdapter(this, fragmentList, titleList)
        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabMypageCategory, binding.viewPager) { tab, position ->
            tab.text = viewPagerAdapter.getPageTitle(position)
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