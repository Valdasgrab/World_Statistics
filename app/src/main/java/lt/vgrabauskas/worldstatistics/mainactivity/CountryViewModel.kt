package lt.vgrabauskas.worldstatistics.mainactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lt.vgrabauskas.worldstatistics.repository.Country
import lt.vgrabauskas.worldstatistics.repository.CountryRepository

class CountryViewModel : ViewModel() {
    private val countryRepository: CountryRepository = CountryRepository.instance
    private val _countryLiveData = MutableLiveData<List<Country>>(mutableListOf())
    private val _filteredCountryLiveData = MutableLiveData<List<Country>>()

    val filteredCountryLiveData: LiveData<List<Country>>
        get() = _filteredCountryLiveData

    fun fetchCountries() {
        viewModelScope.launch {
            val countries = countryRepository.fetchCountries()
            val sortedCountries = countries.sortedBy { it.commonName }
            _countryLiveData.value = sortedCountries
            _filteredCountryLiveData.value = sortedCountries
        }
    }

    fun filterCountries(query: String?) {
        val filteredCountries = _countryLiveData.value?.filter { country ->
            country.commonName.contains(query ?: "", ignoreCase = true)
        } ?: emptyList()
        _filteredCountryLiveData.value = filteredCountries
    }
}
