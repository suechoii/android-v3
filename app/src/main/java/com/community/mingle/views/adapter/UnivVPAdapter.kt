package com.community.mingle.views.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.community.mingle.views.ui.univTotal.*

class UnivVPAdapter(fragment: UnivFragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> UnivFreeFragment()
            1 -> UnivQuestionsFragment()
            2 -> UnivCareerFragment()
            3 -> UnivCouncilFragment()
            else -> UnivHotFragment()
        }
    }
}