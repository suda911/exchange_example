package com.gligent.exchange.domain.repository

import com.gligent.exchange.CoroutineTestRule
import com.gligent.exchange.data.models.Currency
import com.gligent.exchange.data.models.Rates
import com.gligent.exchange.data.models.Result
import com.gligent.exchange.data.providers.CurrencyLocalProvider
import com.gligent.exchange.data.providers.CurrencyRemoteProvider
import com.nhaarman.mockitokotlin2.anyOrNull
import io.objectbox.reactive.DataObserver
import io.objectbox.reactive.DataSubscription
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class CurrencyRepositoryImplTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock
    lateinit var remote: CurrencyRemoteProvider

    @Mock
    lateinit var local: CurrencyLocalProvider

    lateinit var currencyRepositoryImpl: CurrencyRepositoryImpl

    private val resultSuccess = Result<Rates>().apply { data = rates }

    private val resultError = Result<Rates>().apply { error = "Bad!" }

    private val rates = Rates(
        mapOf(
            "RUB" to 1.0,
            "USD" to 2.0
        ), "USD", "21.01.2019"
    )

    private val list: List<Currency> =
        listOf(Currency(ticket = "RUB", course = 1.0), Currency(ticket = "USD", course = 2.0))

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        currencyRepositoryImpl = CurrencyRepositoryImpl(remote, local)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should get all currency if request is success`() = runBlockingTest {
        `when`(remote.getCurrency()).thenReturn(resultSuccess)
        `when`(local.getCurrency()).thenAnswer { list }

        val data = currencyRepositoryImpl.getCurrency()
        assertNotNull(data)
        assertEquals(data, list)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should get all currency if request is failed`() = runBlockingTest {
        `when`(remote.getCurrency()).thenReturn(resultError)
        `when`(local.getCurrency()).thenAnswer { list }

        val data = currencyRepositoryImpl.getCurrency()
        assertNotNull(data)
        assertEquals(data, list)
    }


    @ExperimentalCoroutinesApi
    @Test
    fun `should get all currency if saving to db is failed`() = runBlockingTest {
        `when`(remote.getCurrency()).thenReturn(resultError)
        `when`(local.setList(anyOrNull())).then { Throwable() }
        `when`(local.getCurrency()).thenAnswer { list }

        val data = currencyRepositoryImpl.getCurrency()
        assertNotNull(data)
        assertEquals(data, list)
    }


    @Test
    fun `should return data subscription if call`() {
        val dataSubscription = DataObserver<List<Currency>> {}

        `when`(local.subscribeToCurrency(anyOrNull())).thenReturn(mock(DataSubscription::class.java))

        val result = currencyRepositoryImpl.subscribeToCurrency(dataSubscription)
        assertNotNull(result)
    }
}