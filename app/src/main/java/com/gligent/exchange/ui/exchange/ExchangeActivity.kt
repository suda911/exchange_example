package com.gligent.exchange.ui.exchange

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.gligent.exchange.MyApp
import com.gligent.exchange.R
import com.gligent.exchange.data.models.Currency
import com.gligent.exchange.databinding.ExchangeActivityBinding
import com.gligent.exchange.domain.repository.CurrencyRepository
import com.gligent.exchange.ui.adapters.BasicFragmentStateAdapter
import com.gligent.exchange.ui.adapters.OtherFragmentStateAdapter
import com.gligent.exchange.ui.attachMarginHelper
import com.gligent.exchange.ui.showText
import com.gligent.exchange.ui.viewmodel.factory
import javax.inject.Inject

class ExchangeActivity : AppCompatActivity() {

    @Inject
    lateinit var currencyRepository: CurrencyRepository

    /**
     * ViewModel для обмена данными 1 на все фрагменты
     * */
    val viewModel by lazy {
        val factory = factory {
            ExchangeViewModel(application, currencyRepository)
        }

        ViewModelProviders.of(this, factory).get(ExchangeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApp.netComponent.inject(this)

        val binding = DataBindingUtil
            .setContentView<ExchangeActivityBinding>(this, R.layout.exchange_activity)

        binding.viewModel = viewModel


        viewModel.currencyList.observe(this, Observer<List<Currency>> {
            it?.let {
                binding.basic.adapter = BasicFragmentStateAdapter(
                    it,
                    supportFragmentManager,
                    lifecycle
                )

                binding.other.adapter = OtherFragmentStateAdapter(
                    it,
                    supportFragmentManager,
                    lifecycle
                )
            }
        })

        binding.basic.attachMarginHelper(this)
        binding.other.attachMarginHelper(this)


        binding.basic.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.setPrimaryCurrency(position) // оповещаем viewModel о изменении главной валюты
                viewModel.exchangeCourse()// перерасчёт курса для текущей валюты
            }
        })

        binding.other.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.setSecondaryCurrency(position)// оповещаем viewModel о изменении побочной валюты
                viewModel.exchangeCourse()// перерасчёт курса для текущей валюты
            }
        })

        //установка слушателя для сообщений пользователю
        viewModel.toast.observe(this, Observer { showText(it) })
    }
}
