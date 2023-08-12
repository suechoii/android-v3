package com.community.mingle.views.ui.notification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.community.mingle.R
import com.community.mingle.common.IntentConstants
import com.community.mingle.databinding.ActivityNotificationBinding
import com.community.mingle.service.models.NotiData
import com.community.mingle.utils.base.BaseActivity
import com.community.mingle.viewmodel.HomeViewModel
import com.community.mingle.views.adapter.NotiRVAdapter
import com.community.mingle.views.ui.board.PostActivity
import com.community.mingle.views.ui.market.MarketPostActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiActivity :
    BaseActivity<ActivityNotificationBinding>(R.layout.activity_notification) {

    private val viewModel: HomeViewModel by viewModels()
    private var notiList : ArrayList<NotiData>? = null
    private lateinit var notiListAdapter: NotiRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() {
        binding.notiReturnIv.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initViewModel() {
        binding.viewModel = viewModel

        viewModel.getNotification()

        viewModel.getNotificationSuccess.observe(binding.lifecycleOwner!!) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    viewModel.notiList.observe(binding.lifecycleOwner!!) {
                        Log.d("notiList",notiList.toString())
                        initRV(it as ArrayList<NotiData>?)
                    }
                }
            }
        }
    }

    private fun initRV(notiList: ArrayList<NotiData>?) {
        if (notiList != null) {
            notiListAdapter = NotiRVAdapter(notiList)
            binding.itemNotiVp.adapter = notiListAdapter
            notiListAdapter.setMyItemClickListener(object : NotiRVAdapter.MyItemClickListener {
                override fun onItemClick(post: NotiData) {
                    Log.d("postId",post.postId.toString())
                    viewModel.readNotification(post.boardType, post.notificationId)
                    viewModel.readNotificationSuccess.observe(binding.lifecycleOwner!!) { event ->
                        event.getContentIfNotHandled()?.let {
                            changePostFragment(post)
                        }
                    }
                }
            })
        }
    }


    private fun changePostFragment(notiData: NotiData) {
        val intent : Intent
        if (notiData.boardType == "장터") {
            intent = Intent(this@NotiActivity, MarketPostActivity::class.java)
            intent.putExtra("itemId", notiData.postId)
        } else {
            intent = Intent(this@NotiActivity, PostActivity::class.java)
            intent.putExtra(IntentConstants.BoardType, notiData.boardType)
            intent.putExtra(IntentConstants.CategoryType, notiData.category)
            intent.putExtra("postId", notiData.postId)
        }
        startActivity(intent)
    }

}