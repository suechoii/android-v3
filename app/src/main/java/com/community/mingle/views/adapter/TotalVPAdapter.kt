package com.community.mingle.views.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.community.mingle.model.post.TotalBoardType
import com.community.mingle.views.ui.univTotal.*

class TotalVPAdapter(fragment: TotalFragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (TotalBoardType.parseFromTabPosition(position)) {
            TotalBoardType.Free -> TotalFreeFragment()
            TotalBoardType.Questions -> TotalQuestionsFragment()
            TotalBoardType.MingleNews -> TotalNewsFragment()
        }
    }
}