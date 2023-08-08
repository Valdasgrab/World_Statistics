package lt.vgrabauskas.worldstatistics.mainactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lt.vgrabauskas.worldstatistics.repository.Country
import lt.vgrabauskas.worldstatistics.repository.CountryRepository

class CountryViewModel(private val countryRepository: CountryRepository) : ViewModel() {

    private val _countryLiveData = MutableLiveData<List<Country>>(mutableListOf())
    private val _filteredCountryLiveData = MutableLiveData<List<Country>>()
    private val _fetchingLiveData = MutableLiveData<Boolean>()

    val fetchingLiveData: LiveData<Boolean>
        get() = _fetchingLiveData
    val filteredCountryLiveData: LiveData<List<Country>>
        get() = _filteredCountryLiveData

    fun fetchCountries() {
        _fetchingLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val countries = countryRepository.fetchCountries()
                val sortedCountries = countries.sortedBy { it.commonName }
                withContext(Dispatchers.Main) {
                    _countryLiveData.value = sortedCountries
                    _filteredCountryLiveData.value = sortedCountries
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _fetchingLiveData.postValue(false)
            }
        }
    }

    fun filterCountries(query: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            val filteredCountries = _countryLiveData.value?.filter { country ->
                country.commonName.contains(query ?: "", ignoreCase = true)
            } ?: emptyList()
            withContext(Dispatchers.Main) {
                _filteredCountryLiveData.value = filteredCountries
            }
        }
    }
}
