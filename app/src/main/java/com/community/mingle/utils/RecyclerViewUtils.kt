package com.community.mingle.utils

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


object RecyclerViewUtils {

    // 리사이클러뷰 아이템 간의 간격 설정
//    class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
//        RecyclerView.ItemDecoration() {
//
//        override fun getItemOffsets(
//            outRect: Rect, view: View, parent: RecyclerView,
//            state: RecyclerView.State
//        ) {
//            outRect.bottom = verticalSpaceHeight
//        }
//    }

    // 리사이클러뷰 아이템 간의 간격 설정
    class HorizontalSpaceItemDecoration(private val horizontalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.right = horizontalSpaceHeight
        }
    }

    class DividerItemDecorator(private val mDivider: Drawable):
        RecyclerView.ItemDecoration() {

        private val dividerWidth = mDivider.intrinsicWidth
        private val dividerHeight = mDivider.intrinsicHeight

        override fun getItemOffsets(rect: Rect, v: View, parent: RecyclerView, s: RecyclerView.State) {
            parent.adapter?.let { adapter ->
                val childAdapterPosition = parent.getChildAdapterPosition(v)
                    .let { if (it == RecyclerView.NO_POSITION) return else it }
                rect.bottom = // Add space/"padding" on right side
                    if (childAdapterPosition == adapter.itemCount - 1) dividerHeight / 2 // No "padding"
                    else dividerHeight                                 // Drawable width "padding"
            }
        }
        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            parent.adapter?.let { adapter ->
                parent.children // Displayed children on screen
                    .forEach { view ->
                        val childAdapterPosition = parent.getChildAdapterPosition(view)
                            .let { if (it == RecyclerView.NO_POSITION) return else it }
                        val dividerLeft = parent.paddingLeft
                        val dividerRight = parent.width - parent.paddingRight
                        val childCount = parent.childCount
                        if (childAdapterPosition != adapter.itemCount - 1) {
                            val child = parent.getChildAt(childAdapterPosition)
                            val params = child.layoutParams as RecyclerView.LayoutParams
                            val dividerTop = child.bottom + params.bottomMargin
                            val dividerBottom = dividerTop + mDivider.intrinsicHeight
                            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                            mDivider.draw(c)
                        }
                    }
            }
        }
    }
}