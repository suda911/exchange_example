package com.gligent.exchange.ui.deposit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gligent.exchange.CoroutineTestRule
import com.gligent.exchange.MyApp
import com.gligent.exchange.data.models.Currency
import com.gligent.exchange.domain.repository.CurrencyRepository
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class DepositViewModelTest {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    lateinit var application: MyApp

    @Mock
    var currencyRepository: CurrencyRepository = mock(CurrencyRepository::class.java)

    private lateinit var viewModel: DepositViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = DepositViewModel(application, currencyRepository)
    }

    @Test
    fun `check success update live data`() {
        viewModel.setDeposit(Currency(ticket = "RUB", course = 20.2))
        assertNotNull(viewModel.deposit)
        assertNotNull(viewModel.deposit.get())
        assertEquals(viewModel.deposit.get(), "You have : 100.0")
    }

    @Test
    fun `check failed update live data`() {
        viewModel.setDeposit(null)
        assertNotNull(viewModel.deposit)
        assertNull(viewModel.deposit.get())
    }
}