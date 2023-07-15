package com.community.mingle.views.ui.univTotal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.community.mingle.MingleApplication
import com.community.mingle.R
import com.community.mingle.common.IntentConstants
import com.community.mingle.databinding.FragmentUnivBinding
import com.community.mingle.model.post.UnivBoardType
import com.community.mingle.utils.base.BaseFragment
import com.community.mingle.views.adapter.UnivVPAdapter
import com.community.mingle.views.ui.board.PostWriteActivity
import com.community.mingle.views.ui.search.SearchActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UnivFragment : BaseFragment<FragmentUnivBinding>(R.layout.fragment_univ) {

    private lateinit var tabLayout: TabLayout

    private lateinit var callback: OnBackPressedCallback

    private lateinit var univVPAdapter: UnivVPAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

//                if (binding.boardContentVp.currentItem != 0) {
//                    binding.boardContentVp.currentItem = 0
//                }

                val bottom = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

                bottom.selectedItemId = R.id.homeFragment

            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onResume() {
        Log.d("is it onResume","sue")
        super.onResume()
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
            intent.putExtra(IntentConstants.CategoryType, "잔디밭")
            startActivity(intent)
            //requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.textExp.text = MingleApplication.pref.univName

        binding.searchIv.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initVP() {

        univVPAdapter = UnivVPAdapter(this)

        binding.boardContentVp.adapter = univVPAdapter

        val index= arguments?.getInt("index")

        Log.d("index",index.toString())
        tabLayout = binding.tabBoardname

        TabLayoutMediator(binding.tabBoardname, binding.boardContentVp) { tab, position ->
            tab.text = resources.getString(UnivBoardType.parseFromTabPosition(position).tabNameStringRes)
        }.attach()

        if (index != null) {
            lifecycleScope.launch {
                delay(100)
                binding.boardContentVp.currentItem = index
            }
        }
    }
}