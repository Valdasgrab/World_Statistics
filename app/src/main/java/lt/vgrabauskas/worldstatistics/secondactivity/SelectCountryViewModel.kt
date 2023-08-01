package lt.vgrabauskas.worldstatistics.secondactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lt.vgrabauskas.worldstatistics.repository.Country
import lt.vgrabauskas.worldstatistics.repository.CountryRepository

class SelectCountryViewModel: ViewModel() {
    private val countryRepository: CountryRepository = CountryRepository.instance
    private val _countryLiveData = MutableLiveData<List<Country>>(mutableListOf())


    val countryLiveData: LiveData<List<Country>>
        get() = _countryLiveData


    fun fetchCountries() {
        viewModelScope.launch {
            val countries = countryRepository.fetchCountries()
            val sortedCountries = countries.sortedBy { it.commonName }
            _countryLiveData.value = sortedCountries
        }
    }
}