package com.community.mingle

enum class NotificationType(val title: String, val id: Int) {
    NORMAL("일반 알림", 0),
    EXPANDABLE("확장형 알림", 1),
}