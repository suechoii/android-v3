package com.community.mingle.views.ui.univTotal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.community.mingle.R
import com.community.mingle.common.IntentConstants
import com.community.mingle.databinding.FragmentTotalBinding
import com.community.mingle.model.post.TotalBoardType
import com.community.mingle.utils.base.BaseFragment
import com.community.mingle.views.adapter.TotalVPAdapter
import com.community.mingle.views.ui.board.PostWriteActivity
import com.community.mingle.views.ui.search.SearchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TotalFragment : BaseFragment<FragmentTotalBinding>(R.layout.fragment_total) {

    private lateinit var tabLayout: TabLayout
    private lateinit var callback: OnBackPressedCallback
    private lateinit var totalVPAdapter: TotalVPAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val bottom = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                bottom.selectedItemId = R.id.homeFragment
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVP()
        initView()
    }

    private fun initView() {
        binding.pen.setOnClickListener {
            val intent = Intent(mContext, PostWriteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(IntentConstants.BoardType, "광장")
            startActivity(intent)
        }

        binding.searchIv.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initVP() {
        totalVPAdapter = TotalVPAdapter(this)

        binding.boardContentVp.adapter = totalVPAdapter
        TabLayoutMediator(binding.tabBoardname, binding.boardContentVp) { tab, position ->
            val type = TotalBoardType.parseFromTabPosition(position)
            tab.text = resources.getString(type.tabNameStringRes)
        }.attach()

        tabLayout = binding.tabBoardname
        val index = arguments?.getInt("index") ?: return

        lifecycleScope.launch {
            delay(100)
            binding.boardContentVp.currentItem = index
        }
    }
}