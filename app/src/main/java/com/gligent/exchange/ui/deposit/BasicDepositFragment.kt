package com.gligent.exchange.ui.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.gligent.exchange.MyApp
import com.gligent.exchange.databinding.DepositBasicFragmentBinding
import com.gligent.exchange.ui.exchange.ExchangeViewModel

class BasicDepositFragment : BaseDepositFragment() {

    lateinit var binding: DepositBasicFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DepositBasicFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.depositViewModel = depositViewModel

        return binding.root
    }

    override fun setListeners() {
        binding.amount.addTextChangedListener(textWatcher)
        viewModel.basicAmount.observe(this, Observer { binding.amount.setText(it) })
        binding.amount.onFocusChangeListener = focusChangeListener
    }

    override fun removeListeners() {
        binding.amount.removeTextChangedListener(textWatcher)
        viewModel.basicAmount.removeObservers(this)
        binding.amount.onFocusChangeListener = null
    }

    override fun setTicket(ticket: String) {
        binding.ticket.text = ticket
    }

    override fun changeAmount(amount: String) {
        viewModel.setBasicAmount(amount)
    }

    private val focusChangeListener =
        View.OnFocusChangeListener { _, _ -> viewModel.setFocus(ExchangeViewModel.FOCUS.BASIC) }
}