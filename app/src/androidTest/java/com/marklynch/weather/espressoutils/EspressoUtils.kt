package com.marklynch.weather.espressoutils

import android.view.View
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.rules.TestWatcher
import java.io.File


fun withListSize(size: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun matchesSafely(view: View): Boolean {
            if (view is ListView)
                return view.count === size
            if (view is RecyclerView)
                return view.childCount == size
            return false
        }

        override fun describeTo(description: Description) {
            description.appendText("ListView should have $size items")
        }
    }
}

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

class ViewRefreshingIdlingResource(
    private val swipeRefreshLayout: SwipeRefreshLayout,
    private val expectedRefreshState: Boolean
) :
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

fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
    return RecyclerViewMatcher(recyclerViewId)
}

fun clickViewWithId(viewId: Int) {
    onView(withId(viewId)).perform(click())
}

fun Int.click() {
    onView(withId(this)).perform(ViewActions.click())
}

fun clickViewWithText(viewText: String) {
    onView(withText(viewText)).perform(click())
}

fun String.click() {
    onView(withText(this)).perform(ViewActions.click())
}

fun assertViewHasText(viewId: Int, text: String) {
    onView(withId(viewId)).check(
        ViewAssertions.matches(
            withText(
                text
            )
        )
    )
}

fun assertViewDisplayed(viewId: Int) {
    onView(withId(viewId)).check(
        ViewAssertions.matches(
            isDisplayed()
        )
    )
}

fun assertViewNotDisplayed(viewId: Int) {
    onView(withId(viewId)).check(
        ViewAssertions.matches(
            not(isDisplayed())
        )
    )
}

fun assertViewDisplayed(viewText: String) {
    onView(withText(viewText)).check(
        ViewAssertions.matches(
            isDisplayed()
        )
    )
}

fun clickItemInRecyclerView(recyclerViewId: Int, position: Int) {
    onView(withId(recyclerViewId))
        .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(position, click()))
}

fun assertListSize(listId: Int, size: Int) {
    onView(withId(listId)).check(ViewAssertions.matches(withListSize(size)))
}

fun assertItemInRecyclerViewHasText(listId: Int, position: Int, text: String) {
    onView(withRecyclerView(listId).atPosition(position))
        .check(ViewAssertions.matches(hasDescendant(withText(text))))
}


class ScreenshotTakingRule : TestWatcher() {
    override fun failed(e: Throwable?, description: org.junit.runner.Description) {
        val path = File(
            InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null)?.absolutePath
                    + "/screenshots/" + InstrumentationRegistry.getInstrumentation().targetContext.packageName
        )

        if (!path.exists()) {
            path.mkdirs()
        }

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val filename = description.className + "-" + description.methodName + ".png"
        device.takeScreenshot(File(path, filename))
    }
}

