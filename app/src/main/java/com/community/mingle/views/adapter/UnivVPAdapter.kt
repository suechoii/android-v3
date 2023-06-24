package com.community.mingle.views.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.community.mingle.model.univ.UnivBoardType
import com.community.mingle.views.ui.univTotal.*

class UnivVPAdapter(fragment: UnivFragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (UnivBoardType.parseFromTabPosition(position)) {
            UnivBoardType.Free -> UnivFreeFragment()
            UnivBoardType.Questions -> UnivQuestionsFragment()
            UnivBoardType.Council -> UnivCouncilFragment()
        }
    }
}