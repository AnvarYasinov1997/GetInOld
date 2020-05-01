package com.wellcome.main.wrapper

sealed class Delegate {
    data class UserDelegate(val saved: Boolean, val rated: Boolean) : Delegate()
    data class UserStoryDelegate(val liked: Boolean) : Delegate()
    data class TimeDelegate(val open: Boolean) : Delegate()
    data class StoreReviewDelegate(val allowed: Boolean) : Delegate()
}