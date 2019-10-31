package com.gligent.exchange.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gligent.exchange.data.models.Currency

abstract class ViewPagerStateAdapter(
    private val list: List<Currency>,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    abstract fun createChild(position: Int): Fragment

    override fun createFragment(position: Int) = createChild(position)

    override fun getItemCount(): Int = list.size
}