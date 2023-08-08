package lt.vgrabauskas.worldstatistics.mainactivity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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

@ExperimentalCoroutinesApi
class CountryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CountryViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    // Mock objects
    private lateinit var mockCountryRepository: CountryRepository

    @Before
    fun setup() {
        // Initialize mock objects
        mockCountryRepository = mockk()

        Dispatchers.setMain(testDispatcher)

        viewModel = CountryViewModel(mockCountryRepository)
    }

    @Test
    fun testFetchCountries_Success() = testDispatcher.runBlockingTest {
        // Arrange
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

        // Act
        viewModel.fetchCountries()

        // Assert
        coEvery { mockCountryRepository.fetchCountries() }
        coEvery { fetchingObserver.onChanged(true) }
        coEvery { countriesObserver.onChanged(countries) }
        coEvery { filteredCountriesObserver.onChanged(countries) }
    }

    @Test
    fun testFetchCountries_Failure() = testDispatcher.runBlockingTest {
        // Arrange
        coEvery { mockCountryRepository.fetchCountries() } throws Exception("Test exception")

        val fetchingObserver = mockk<Observer<Boolean>>(relaxed = true)

        viewModel.fetchingLiveData.observeForever(fetchingObserver)

        // Act
        viewModel.fetchCountries()

        // Assert
        coVerify { fetchingObserver.onChanged(true) }
        coVerify { fetchingObserver.onChanged(false) }
    }

    @Test
    fun testFetchCountries_EmptyResponse() = testDispatcher.runBlockingTest {
        // Arrange
        val countries = emptyList<Country>()
        coEvery { mockCountryRepository.fetchCountries() } returns countries

        val fetchingObserver = mockk<Observer<Boolean>>(relaxed = true)
        val countriesObserver = mockk<Observer<List<Country>>>(relaxed = true)
        val filteredCountriesObserver = mockk<Observer<List<Country>>>(relaxed = true)

        viewModel.fetchingLiveData.observeForever(fetchingObserver)
        viewModel.filteredCountryLiveData.observeForever(filteredCountriesObserver)
        viewModel.filteredCountryLiveData.observeForever(countriesObserver)

        // Act
        viewModel.fetchCountries()

        // Assert
        coVerify { fetchingObserver.onChanged(true) }
        coVerify { countriesObserver.onChanged(countries) }
        coVerify { filteredCountriesObserver.onChanged(countries) }
        coVerify { fetchingObserver.onChanged(false) }
    }

    @Test
    fun testFetchCountries_Exception() = testDispatcher.runBlockingTest {
        // Arrange
        coEvery { mockCountryRepository.fetchCountries() } throws Exception("Test exception")

        val fetchingObserver = mockk<Observer<Boolean>>(relaxed = true)

        viewModel.fetchingLiveData.observeForever(fetchingObserver)

        // Act
        viewModel.fetchCountries()

        // Assert
        coVerify { fetchingObserver.onChanged(true) }
        coVerify { fetchingObserver.onChanged(false) }
    }
}
