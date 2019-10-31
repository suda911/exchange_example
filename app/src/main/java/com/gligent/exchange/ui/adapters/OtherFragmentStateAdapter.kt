package com.gligent.exchange.ui.adapters

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.gligent.exchange.data.models.Currency
import com.gligent.exchange.ui.deposit.OtherDepositFragment

class OtherFragmentStateAdapter(
    private val list: List<Currency>,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : ViewPagerStateAdapter(list, fm, lifecycle) {
    override fun createChild(position: Int): Fragment {
        return OtherDepositFragment().apply {
            arguments = bundleOf(
                "currency" to list[position].ticket
            )
        }
    }
}