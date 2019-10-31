package com.gligent.exchange.ui

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.gligent.exchange.R
import com.gligent.exchange.ui.adapters.HorizontalMarginItemDecoration

/**
 * Декораторы для viewPager2
 * */
fun ViewPager2.attachMarginHelper(context: Context) {
    offscreenPageLimit = 1

    val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
    val currentItemHorizontalMarginPx =
        resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
    val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
    val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
        page.translationX = -pageTranslationX * position
        page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
    }

    setPageTransformer(pageTransformer)

    val itemDecoration = HorizontalMarginItemDecoration(
        context,
        R.dimen.viewpager_current_item_horizontal_margin
    )

    addItemDecoration(itemDecoration)
}

/**
 * Отображение тоста
 * */
fun FragmentActivity.showText(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}