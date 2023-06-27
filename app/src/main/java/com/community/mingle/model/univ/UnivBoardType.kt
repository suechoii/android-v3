package com.community.mingle.model.univ

import androidx.annotation.StringRes

sealed class UnivBoardType(
    val tabPosition: Int,
    @StringRes val tabNameStringRes: Int,
) {

    object Free : UnivBoardType(0, com.community.mingle.R.string.univ_board_tab_name_free)
    object Questions : UnivBoardType(1, com.community.mingle.R.string.univ_board_tab_name_questions)
    object Council : UnivBoardType(2, com.community.mingle.R.string.univ_board_tab_name_council)

    companion object {

        fun parseFromTabPosition(tabPosition: Int): UnivBoardType {
            return when (tabPosition) {
                Free.tabPosition -> Free
                Questions.tabPosition -> Questions
                Council.tabPosition -> Council
                else -> throw IllegalArgumentException("Invalid tabPosition: $tabPosition")
            }
        }
    }
}

