package com.community.mingle.views.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.community.mingle.views.ui.settings.MarketUserPostActivity
import com.community.mingle.views.ui.settings.ReservedFragment
import com.community.mingle.views.ui.settings.SellingFragment
import com.community.mingle.views.ui.settings.SoldFragment

class MarketMyPageVPAdapter(activity: MarketUserPostActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> SellingFragment()
            1 -> ReservedFragment()
            else -> SoldFragment()
        }
    }

}