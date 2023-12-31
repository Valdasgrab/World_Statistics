package lt.vgrabauskas.worldstatistics.mainactivity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import lt.vgrabauskas.worldstatistics.repository.Country
import lt.vgrabauskas.worldstatistics.repository.CountryRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class CountryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var viewModel: CountryViewModel
    private val testDispatcher = TestCoroutineDispatcher()
    private lateinit var mockCountryRepository: CountryRepository

    @Before
    fun setup() {
        mockCountryRepository = mockk()
        Dispatchers.setMain(testDispatcher)
        viewModel = CountryViewModel(mockCountryRepository)
    }

    @Test
    fun testFetchCountries_Success() = testDispatcher.runBlockingTest {
        val countries = listOf(
            Country(Country.CountryName("Country 1"), null, 1000000, null, null, null, null, null),
            Country(Country.CountryName("Country 2"), null, 2000000, null, null, null, null, null)
        )
        coEvery { mockCountryRepository.fetchCountries() } returns countries
        val fetchingObserver = mockk<Observer<Boolean>>(relaxed = true)
        val countriesObserver = mockk<Observer<List<Country>>>(relaxed = true)
        val filteredCountriesObserver = mockk<Observer<List<Country>>>(relaxed = true)
        viewModel.fetchingLiveData.observeForever(fetchingObserver)
        viewModel.filteredCountryLiveData.observeForever(filteredCountriesObserver)
        viewModel.filteredCountryLiveData.observeForever(countriesObserver)
        viewModel.fetchCountries()
        coEvery { mockCountryRepository.fetchCountries() }
        coEvery { fetchingObserver.onChanged(true) }
        coEvery { countriesObserver.onChanged(countries) }
        coEvery { filteredCountriesObserver.onChanged(countries) }
    }

    @Test
    fun testFetchCountries_Failure() = testDispatcher.runBlockingTest {
        coEvery { mockCountryRepository.fetchCountries() } throws Exception("Test exception")
        val fetchingObserver = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.fetchingLiveData.observeForever(fetchingObserver)
        viewModel.fetchCountries()
        coVerify { fetchingObserver.onChanged(true) }
        coVerify { fetchingObserver.onChanged(false) }
    }

    @Test
    fun testFetchCountries_EmptyResponse() = testDispatcher.runBlockingTest {
        val countries = emptyList<Country>()
        coEvery { mockCountryRepository.fetchCountries() } returns countries
        val fetchingObserver = mockk<Observer<Boolean>>(relaxed = true)
        val countriesObserver = mockk<Observer<List<Country>>>(relaxed = true)
        val filteredCountriesObserver = mockk<Observer<List<Country>>>(relaxed = true)
        viewModel.fetchingLiveData.observeForever(fetchingObserver)
        viewModel.filteredCountryLiveData.observeForever(filteredCountriesObserver)
        viewModel.filteredCountryLiveData.observeForever(countriesObserver)
        viewModel.fetchCountries()
        coVerify { fetchingObserver.onChanged(true) }
        coVerify { countriesObserver.onChanged(countries) }
        coVerify { filteredCountriesObserver.onChanged(countries) }
        coVerify { fetchingObserver.onChanged(false) }
    }

    @Test
    fun testFetchCountries_Exception() = testDispatcher.runBlockingTest {
        coEvery { mockCountryRepository.fetchCountries() } throws Exception("Test exception")
        val fetchingObserver = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.fetchingLiveData.observeForever(fetchingObserver)
        viewModel.fetchCountries()
        coVerify { fetchingObserver.onChanged(true) }
        coVerify { fetchingObserver.onChanged(false) }
    }
    @Test
    fun testFilterCountries_QueryMatchesCountry() = testDispatcher.runBlockingTest {
        val countries = listOf(
            Country(Country.CountryName("Country 1"), null, 1000000, null, null, null, null, null),
            Country(Country.CountryName("Country 2"), null, 2000000, null, null, null, null, null)
        )
        val countryLiveData = MutableLiveData<List<Country>>()
        countryLiveData.value = countries
        val countryLiveDataField = CountryViewModel::class.java.getDeclaredField("_countryLiveData")
        countryLiveDataField.isAccessible = true
        countryLiveDataField.set(viewModel, countryLiveData)
        val query = "Country 1"
        viewModel.filterCountries(query)
        val filteredCountries = LiveDataTestUtil.getValue(viewModel.filteredCountryLiveData)
        assertEquals(listOf(countries[0]), filteredCountries)
    }

    @Test
    fun testFilterCountries_EmptyQuery_ReturnsAllCountries() = testDispatcher.runBlockingTest {
        val countries = listOf(
            Country(Country.CountryName("Country 1"), null, 1000000, null, null, null, null, null),
            Country(Country.CountryName("Country 2"), null, 2000000, null, null, null, null, null)
        )
        val countryLiveData = MutableLiveData<List<Country>>()
        countryLiveData.value = countries
        val countryLiveDataField = CountryViewModel::class.java.getDeclaredField("_countryLiveData")
        countryLiveDataField.isAccessible = true
        countryLiveDataField.set(viewModel, countryLiveData)
        val emptyQuery = ""
        viewModel.filterCountries(emptyQuery)
        val filteredCountries = LiveDataTestUtil.getValue(viewModel.filteredCountryLiveData)
        assertEquals(countries, filteredCountries)
    }

    @Test
    fun testFilterCountries_NoMatches_ReturnsEmptyList() = testDispatcher.runBlockingTest {
        val countries = listOf(
            Country(Country.CountryName("Country 1"), null, 1000000, null, null, null, null, null),
            Country(Country.CountryName("Country 2"), null, 2000000, null, null, null, null, null)
        )
        val countryLiveData = MutableLiveData<List<Country>>()
        countryLiveData.value = countries
        val countryLiveDataField = CountryViewModel::class.java.getDeclaredField("_countryLiveData")
        countryLiveDataField.isAccessible = true
        countryLiveDataField.set(viewModel, countryLiveData)
        val query = "XYZ"
        viewModel.filterCountries(query)
        val filteredCountries = LiveDataTestUtil.getValue(viewModel.filteredCountryLiveData)
        assertEquals(emptyList<Country>(), filteredCountries)
    }
}
object LiveDataTestUtil {
    fun <T> getValue(liveData: LiveData<T>): T? {
        var value: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(t: T) {
                value = t
                latch.countDown()
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)
        return value
    }
}
