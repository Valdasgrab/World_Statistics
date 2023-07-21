package lt.vgrabauskas.worldstatistics.mainactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import lt.vgrabauskas.worldstatistics.repository.Country
import lt.vgrabauskas.worldstatistics.repository.CountryRepository

class CountryViewModel : ViewModel() {
    private val countryRepository: CountryRepository = CountryRepository.instance
    private val _countryLiveData = MutableLiveData<List<Country>>(mutableListOf())

    val countryLiveData: LiveData<List<Country>>
        get() = _countryLiveData

    fun fetchCountries() {
        if (countryLiveData.value == null || _countryLiveData.value?.isEmpty() == true) {
            CountryRepository.instance.addDummyListOfCountries()
        }
        _countryLiveData.value = countryRepository.countries
    }

}