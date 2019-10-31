package com.gligent.exchange.ui.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.gligent.exchange.databinding.DepositOtherFragmentBinding
import com.gligent.exchange.ui.exchange.ExchangeViewModel

class OtherDepositFragment : BaseDepositFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DepositOtherFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.depositViewModel = depositViewModel

        return binding.root
    }

    override fun setListeners() {
        binding.amount.addTextChangedListener(textWatcher)
        viewModel.otherAmount.observe(this, Observer { binding.amount.setText(it) })
        binding.amount.onFocusChangeListener = focusChangeListener
    }

    override fun removeListeners() {
        binding.amount.removeTextChangedListener(textWatcher)
        viewModel.otherAmount.removeObservers(this)
        binding.amount.onFocusChangeListener = null
    }

    override fun setTicket(ticket: String) {
        binding.ticket.text = ticket
    }

    override fun changeAmount(amount: String) {
        viewModel.setOtherAmount(amount)
    }

    lateinit var binding: DepositOtherFragmentBinding


    private val focusChangeListener =
        View.OnFocusChangeListener { _, _ -> viewModel.setFocus(ExchangeViewModel.FOCUS.OTHER) }
}