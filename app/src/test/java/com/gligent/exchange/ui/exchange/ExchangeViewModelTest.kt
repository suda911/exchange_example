package com.gligent.exchange.ui.exchange

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.gligent.exchange.CoroutineTestRule
import com.gligent.exchange.MyApp
import com.gligent.exchange.R
import com.gligent.exchange.data.models.Currency
import com.gligent.exchange.domain.repository.CurrencyRepository
import com.gligent.exchange.observeOnce
import com.nhaarman.mockitokotlin2.anyOrNull
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations


class ExchangeViewModelTest {

    companion object {

        const val balance_is_empty = "Не достаточно средста на балансе"
        const val need_set_amount = "Необходимо ввести нужно сумму!"
        const val transaction_success = "Превосходно, конвертация успешно проведена!"
        const val transaction_failed = "Мы не смогли провести конвертацию :("
        const val error_one = "Это один и тот же счёт, операция невозможна"
        const val sum_is_negative = "Сумма не может быть отрицательной"
        const val download_exception = "Произошла ощибка при загрузке данных"
        const val error_convert = "Произошла ошибка при конвертации"
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    lateinit var application: MyApp

    @Mock
    var currencyRepository: CurrencyRepository = mock(CurrencyRepository::class.java)

    private lateinit var viewModel: ExchangeViewModel

    private val list = listOf(
        Currency(ticket = "RUB", course = 20.2, deposit = 20.0),
        Currency(ticket = "USD", course = 13.2, deposit = 10.2)
    )

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = ExchangeViewModel(application, currencyRepository)
        `when`(viewModel.getApplication<MyApp>().getString(R.string.balance_is_empty)).thenReturn(
            balance_is_empty
        )
        `when`(viewModel.getApplication<MyApp>().getString(R.string.need_set_amount)).thenReturn(
            need_set_amount
        )
        `when`(viewModel.getApplication<MyApp>().getString(R.string.transaction_success)).thenReturn(
            transaction_success
        )
        `when`(viewModel.getApplication<MyApp>().getString(R.string.transaction_failed)).thenReturn(
            transaction_failed
        )
        `when`(viewModel.getApplication<MyApp>().getString(R.string.error_one)).thenReturn(
            error_one
        )
        `when`(viewModel.getApplication<MyApp>().getString(R.string.sum_is_negative)).thenReturn(
            sum_is_negative
        )
        `when`(viewModel.getApplication<MyApp>().getString(R.string.download_exception)).thenReturn(
            download_exception
        )
        `when`(viewModel.getApplication<MyApp>().getString(R.string.error_convert)).thenReturn(
            error_convert
        )
    }

    @Test
    fun `check active observables`() {
        val observer = mock(Observer::class.java) as Observer<List<Currency>>
        viewModel.currencyList.observeForever(observer)
        assertTrue(viewModel.currencyList.hasActiveObservers())
    }

    @Test
    fun `check havn't observables`() {
        assertFalse(viewModel.currencyList.hasActiveObservers())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `check answer from repository`() = runBlockingTest {
        `when`(currencyRepository.getCurrency()).thenReturn(list)
        val result = currencyRepository.getCurrency()

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `check update live data`() {

        assertNotNull(viewModel.basicAmount)

        assertFalse(viewModel.basicAmount.hasActiveObservers())
        assertFalse(viewModel.basicAmount.hasObservers())

        val observer = mock(Observer::class.java) as Observer<String>
        viewModel.basicAmount.observeForever(observer)

        assertNull(viewModel.basicAmount.value)

        assertTrue(viewModel.basicAmount.hasActiveObservers())

        viewModel.basicAmount.value = "100"

        assertNotNull(viewModel.basicAmount.value)
        assertEquals(viewModel.basicAmount.value, "100")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `fetch currency should update live data`() = runBlockingTest {
        `when`(currencyRepository.getCurrency()).thenAnswer { list }

        assertNotNull(viewModel.currencyList)
        assertFalse(viewModel.currencyList.hasObservers())

        val observer = mock(Observer::class.java) as Observer<List<Currency>>
        viewModel.currencyList.observeForever(observer)
        viewModel.getCurrency()

        assertTrue(viewModel.currencyList.hasObservers())
        assertTrue(viewModel.currencyList.hasActiveObservers())

        viewModel.currencyList.observeOnce { list -> assertTrue(list.isNotEmpty()) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `fetch currency with error should show toast`() = runBlockingTest {
        assertNotNull(viewModel.currencyList)
        assertFalse(viewModel.currencyList.hasObservers())

        val observer = mock(Observer::class.java) as Observer<List<Currency>>
        viewModel.currencyList.observeForever(observer)
        viewModel.setCurrency(emptyList())

        assertTrue(viewModel.currencyList.hasObservers())
        assertTrue(viewModel.currencyList.hasActiveObservers())

        assertNotNull(viewModel.toast.value)
        viewModel.currencyList.observeOnce { list -> assertTrue(list.isEmpty()) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `fetch currency with null should show toast`() = runBlockingTest {
        assertNotNull(viewModel.currencyList)
        assertFalse(viewModel.currencyList.hasObservers())

        val observer = mock(Observer::class.java) as Observer<List<Currency>>
        viewModel.currencyList.observeForever(observer)
        viewModel.setCurrency(null)

        assertTrue(viewModel.currencyList.hasObservers())
        assertTrue(viewModel.currencyList.hasActiveObservers())

        assertNull(viewModel.toast.value)
    }

    @Test
    fun `check exchange with same course`() {
        viewModel.currencyList.value = list

        assertNotNull(viewModel.currencyList.value)

        val observer = mock(Observer::class.java) as Observer<String>
        viewModel.toast.observeForever(observer)

        viewModel.setPrimaryCurrency(0)
        viewModel.setSecondaryCurrency(0)

        viewModel.exchange()

        assertNotNull(viewModel.toast.value)
        assertEquals(viewModel.toast.value, error_one)

    }

    @ExperimentalCoroutinesApi
    @Test
    fun `check amount more when have money`() = runBlockingTest {
        `when`(currencyRepository.getCurrency(anyOrNull())).thenAnswer {
            Currency(
                ticket = "RUB",
                course = 20.2,
                deposit = 20.0
            )
        }

        viewModel.currencyList.value = list

        assertNotNull(viewModel.currencyList.value)

        val observer = mock(Observer::class.java) as Observer<String>
        viewModel.toast.observeForever(observer)

        viewModel.setPrimaryCurrency(0)
        viewModel.setSecondaryCurrency(1)

        viewModel.setBasicAmount("30")

        viewModel.exchange()

        assertNotNull(viewModel.toast.value)
        assertEquals(viewModel.toast.value, balance_is_empty)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `check amount basic currency is negative`() = runBlockingTest {
        viewModel.currencyList.value = list

        assertNotNull(viewModel.currencyList.value)

        val observer = mock(Observer::class.java) as Observer<String>
        viewModel.toast.observeForever(observer)

        viewModel.setPrimaryCurrency(0)
        viewModel.setSecondaryCurrency(1)

        viewModel.setBasicAmount("-30")

        viewModel.exchange()

        assertNotNull(viewModel.toast.value)
        assertEquals(viewModel.toast.value, sum_is_negative)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `update deposit with error`() = runBlockingTest {

        `when`(
            viewModel.validateDeposit(
                anyOrNull(),
                15.0
            )
        ).thenAnswer { true }

        `when`(currencyRepository.getCurrency(anyOrNull()))
            .thenAnswer {
                Currency(
                    ticket = "RUB",
                    course = 20.2,
                    deposit = 20.0
                )
            }

        `when`(
            currencyRepository.updateDeposit(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyDouble()
            )
        ).thenReturn(false)

        viewModel.currencyList.value = list

        assertNotNull(viewModel.currencyList.value)

        val observer = mock(Observer::class.java) as Observer<String>
        viewModel.toast.observeForever(observer)

        viewModel.setPrimaryCurrency(0)
        viewModel.setSecondaryCurrency(1)

        viewModel.setBasicAmount("15")

        viewModel.exchange()

        assertNotNull(viewModel.toast.value)
        assertEquals(viewModel.toast.value, transaction_failed)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `update deposit with success`() = runBlockingTest {

        `when`(
            viewModel.validateDeposit(
                anyOrNull(),
                15.0
            )
        ).thenAnswer { true }

        `when`(currencyRepository.getCurrency(anyOrNull()))
            .thenAnswer {
                Currency(
                    ticket = "RUB",
                    course = 20.2,
                    deposit = 20.0
                )
            }

        `when`(
            currencyRepository.updateDeposit(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyDouble(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyDouble()
            )
        ).thenReturn(true)

        viewModel.currencyList.value = list

        assertNotNull(viewModel.currencyList.value)

        val observer = mock(Observer::class.java) as Observer<String>
        viewModel.toast.observeForever(observer)

        viewModel.setPrimaryCurrency(0)
        viewModel.setSecondaryCurrency(1)

        viewModel.setBasicAmount("15")

        viewModel.exchange()

        assertNull(viewModel.basicAmount.value)
        assertNull(viewModel.otherAmount.value)

        assertNotNull(viewModel.toast.value)
        assertEquals(viewModel.toast.value, transaction_success)
    }

    @Test
    fun `should update basicCourse and OtherCourse if call exchangeCourse`() {
        viewModel.currencyList.value = list

        assertNotNull(viewModel.currencyList.value)

        assertNotNull(viewModel.basicCourse)
        assertNotNull(viewModel.otherCourse)

        viewModel.setPrimaryCurrency(0)
        viewModel.setSecondaryCurrency(1)

        viewModel.exchangeCourse()

        assertNotNull(viewModel.basicCourse.get())
        assertEquals(viewModel.basicCourse.get(), "1 = 0.66")
        assertNotNull(viewModel.otherCourse.get())
        assertEquals(viewModel.otherCourse.get(), "1 = 1.54")
    }

    @Test
    fun `should don't crash if basicCurrency and OtherCurrency us null`() {
        viewModel.currencyList.value = list

        assertNotNull(viewModel.currencyList.value)

        assertNotNull(viewModel.basicCourse)
        assertNotNull(viewModel.otherCourse)

        viewModel.exchangeCourse()

        assertNull(viewModel.basicCourse.get())
        assertNull(viewModel.otherCourse.get())
    }
}