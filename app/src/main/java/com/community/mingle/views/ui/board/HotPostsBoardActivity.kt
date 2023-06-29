package com.community.mingle.views.ui.board

import android.os.Bundle
import androidx.activity.viewModels
import com.community.mingle.R
import com.community.mingle.databinding.ActivityHotPostsBoardBinding
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.HotPostsBoardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HotPostsBoardActivity : BaseActivity<ActivityHotPostsBoardBinding>(R.layout.activity_hot_posts_board) {

    private val hotPostViewModel: HotPostsBoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun initSwipeRefresh() {
        binding.swipeRefresh.apply {
            isRefreshing = false
            setOnRefreshListener {  }
        }
    }
}