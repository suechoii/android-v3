package com.community.mingle.model.post

import androidx.annotation.StringRes
import com.community.mingle.R

sealed class TotalBoardType(
    val tabPosition: Int,
    @StringRes val tabNameStringRes: Int,
) {
    object Free : TotalBoardType(tabPosition = 0, tabNameStringRes = R.string.total_board_tab_name_free)
    object Questions : TotalBoardType(tabPosition = 1, tabNameStringRes = R.string.total_board_tab_name_questions)
    object MingleNews : TotalBoardType(tabPosition = 2, tabNameStringRes = R.string.total_board_tab_name_mingle_news)

    companion object {
        fun parseFromTabPosition(
            tabPosition: Int
        ): TotalBoardType {
            return when (tabPosition) {
                Free.tabPosition -> Free
                Questions.tabPosition -> Questions
                MingleNews.tabPosition -> MingleNews
                else -> throw IllegalStateException("Unexpected position $tabPosition")
            }
        }
    }
}