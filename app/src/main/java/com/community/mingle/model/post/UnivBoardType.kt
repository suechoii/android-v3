package com.community.mingle.model.post

import androidx.annotation.StringRes
import com.community.mingle.R

sealed class UnivBoardType(
    val tabPosition: Int,
    @StringRes val tabNameStringRes: Int,
) {
    object All : UnivBoardType(tabPosition = 0, tabNameStringRes = R.string.univ_board_tab_name_all)

    object Free : UnivBoardType(1, com.community.mingle.R.string.univ_board_tab_name_free)
    object Questions : UnivBoardType(2, com.community.mingle.R.string.univ_board_tab_name_questions)
    object Council : UnivBoardType(3, com.community.mingle.R.string.univ_board_tab_name_council)

    companion object {

        fun parseFromTabPosition(tabPosition: Int): UnivBoardType {
            return when (tabPosition) {
                Free.tabPosition -> Free
                Questions.tabPosition -> Questions
                Council.tabPosition -> Council
                All.tabPosition -> All
                else -> throw IllegalArgumentException("Invalid tabPosition: $tabPosition")
            }
        }
    }
}

