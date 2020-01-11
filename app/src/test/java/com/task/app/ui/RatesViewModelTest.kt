package com.task.app.ui

import com.task.app.R
import com.task.app.TestBase
import com.task.app.base.MockSchedulersProvider
import com.task.app.service.CurrencyRateRepository
import com.task.app.ui.list.RatesAdapter
import com.task.app.ui.list.RatesItem
import com.task.app.ui.support.OnClickRatesItemObservable
import com.task.app.ui.support.OnTextWatcherObservable
import io.reactivex.Single
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import java.util.Currency

class RatesViewModelTest : TestBase() {

    @Mock
    lateinit var placeHolderRatesItemUseCase: GetPlaceHolderRatesItemUseCase
    @Mock
    lateinit var serverRatesItemUseCase: GetServerRatesItemUseCase
    @Mock
    lateinit var adapter: RatesAdapter

    private val schedulersProvider = MockSchedulersProvider()
    private val onClickRatesItemObservable = OnClickRatesItemObservable()
    private val onTextWatcherObservable = OnTextWatcherObservable(schedulersProvider)


    private lateinit var viewModel: RatesViewModel

    override fun setup() {
        super.setup()
        viewModel = RatesViewModel(
            schedulersProvider, placeHolderRatesItemUseCase, serverRatesItemUseCase, onClickRatesItemObservable, onTextWatcherObservable,
            adapter
        )
    }

    @Test
    fun observeOnItemClick_firstLaunchEmitBaseItem_noUpdateAdapter() {
        val item = CurrencyRateRepository.BASE_RATES_ITEM
        val emptyData = emptyList<RatesItem>()
        given(adapter.moveSelectedItemToTop(item))
            .willReturn(Single.just(emptyData))

        val testObserver = viewModel.observeOnItemClick().test()

        testObserver.assertNoErrors()
            .assertNotTerminated()
            .assertValueCount(1)
            .assertValue(emptyData)

        // Try to emit the same value, won't emit
        onClickRatesItemObservable.emitItem(item)

        testObserver
            .assertNotTerminated()
            .assertNoErrors()
            .assertValueCount(1)
            .assertValues(emptyData)
            .dispose()
    }

    @Test
    fun observeOnItemClick_clickTheFirstItem_noUpdateAdapter() {
        val emptyData = emptyList<RatesItem>()
        given(adapter.moveSelectedItemToTop(CurrencyRateRepository.BASE_RATES_ITEM))
            .willReturn(Single.just(emptyData))
        val testObserver = viewModel.observeOnItemClick().test()

        testObserver.assertNoErrors()
            .assertNotTerminated()
            .assertValueCount(1)
            .assertValue(emptyData)

        val list = testData()
        given(adapter.moveSelectedItemToTop(list[0]))
            .willReturn(Single.just(emptyData))

        // Emit the firs item
        onClickRatesItemObservable.emitItem(list[0])

        testObserver
            .assertNotTerminated()
            .assertNoErrors()
            .assertValueCount(2)
            .assertValues(emptyData, emptyData)
            .dispose()
    }

    private fun testData(): List<RatesItem> {
        return listOf(
            RatesItem("AUD", Currency.getInstance("AUD").displayName, "", R.drawable.ic_flag_aud),
            RatesItem("BGN", Currency.getInstance("BGN").displayName, "", R.drawable.ic_flag_bgn),
            RatesItem("BRL", Currency.getInstance("BRL").displayName, "", R.drawable.ic_flag_brl),
            RatesItem("CAD", Currency.getInstance("CAD").displayName, "", R.drawable.ic_flag_cad),
            RatesItem("CNY", Currency.getInstance("CNY").displayName, "", R.drawable.ic_flag_cny)
        )
    }
}