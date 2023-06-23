package com.community.mingle.views.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.community.mingle.views.ui.univTotal.*

class TotalVPAdapter(fragment: TotalFragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> TotalFreeFragment()
            1 -> TotalQuestionsFragment()
            2 -> TotalCareerFragment()
            3 -> TotalNewsFragment()
            else -> TotalHotFragment()
        }
    }
}