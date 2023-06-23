package com.community.mingle.views.ui.search

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SearchVPAdapter(activity: SearchActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> SearchUnivFragment()
            else -> SearchTotalFragment()
        }
    }

}