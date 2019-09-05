package com.marklynch.weather.activity

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

class ViewVisibilityIdlingResource(private val mView: View, private val mExpectedVisibility: Int) :
    IdlingResource {

    private var mIdle: Boolean = false
    private var mResourceCallback: IdlingResource.ResourceCallback? = null

    init {
        this.mIdle = false
        this.mResourceCallback = null
    }

    override fun getName(): String {
        return ViewVisibilityIdlingResource::class.java.simpleName
    }

    override fun isIdleNow(): Boolean {
        mIdle = mIdle || mView.visibility == mExpectedVisibility

        if (mIdle) {
            if (mResourceCallback != null) {
                mResourceCallback!!.onTransitionToIdle()
            }
        }

        return mIdle
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        mResourceCallback = resourceCallback
    }

}

class ViewRefreshingIdlingResource(private val swipeRefreshLayout: SwipeRefreshLayout, private val expectedRefreshState: Boolean) :
    IdlingResource {

    private var mIdle: Boolean = false
    private var mResourceCallback: IdlingResource.ResourceCallback? = null

    init {
        this.mIdle = false
        this.mResourceCallback = null
    }

    override fun getName(): String {
        return ViewVisibilityIdlingResource::class.java.simpleName
    }

    override fun isIdleNow(): Boolean {
        mIdle = mIdle || swipeRefreshLayout.isRefreshing == expectedRefreshState

        if (mIdle) {
            if (mResourceCallback != null) {
                mResourceCallback!!.onTransitionToIdle()
            }
        }

        return mIdle
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        mResourceCallback = resourceCallback
    }

}

object SwipeRefreshLayoutMatchers {
    @JvmStatic
    fun isRefreshing(): Matcher<View> {
        return object : BoundedMatcher<View, SwipeRefreshLayout>(
            SwipeRefreshLayout::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("is refreshing")
            }

            override fun matchesSafely(view: SwipeRefreshLayout): Boolean {
                return view.isRefreshing
            }
        }
    }

    @JvmStatic
    fun isNotRefreshing(): Matcher<View> {
        return object : BoundedMatcher<View, SwipeRefreshLayout>(
            SwipeRefreshLayout::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("is not refreshing")
            }

            override fun matchesSafely(view: SwipeRefreshLayout): Boolean {
                return !view.isRefreshing
            }
        }
    }
}