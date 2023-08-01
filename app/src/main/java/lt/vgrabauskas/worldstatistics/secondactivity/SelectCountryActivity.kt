package lt.vgrabauskas.worldstatistics.secondactivity


import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import lt.vgrabauskas.worldstatistics.databinding.ActivitySelectCountryBinding
import lt.vgrabauskas.worldstatistics.repository.Country

class SelectCountryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectCountryBinding
    private lateinit var initialCountry: Country
    private lateinit var selectedCountry: Country
    private lateinit var countryViewModel: SelectCountryViewModel
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var countriesForComparisonFiltered: List<Country>
    private var isCountrySelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialCountry = intent.getParcelableExtra("selectedCountry")!!

        countryViewModel = ViewModelProvider(this).get(SelectCountryViewModel::class.java)
        countryViewModel.fetchCountries()

        val countriesListView: ListView = binding.countriesListViewComparison
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        countriesListView.adapter = adapter

        val searchView: SearchView = binding.searchView
        searchView.queryHint = "Search countries"
        searchView.setOnClickListener {
            searchView.isIconified = false
            searchView.requestFocus()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCountries(newText)
                return true
            }
        })

        countryViewModel.countryLiveData.removeObservers(this)

        val observer = Observer<List<Country>> { countries ->
            countriesForComparisonFiltered = countries.filter { it != initialCountry }
            adapter.clear()
            adapter.addAll(countriesForComparisonFiltered.map { country -> country.commonName })
            countriesListView.setOnItemClickListener { _, _, position, _ ->
                val secondSelectedCountry = countriesForComparisonFiltered.getOrNull(position)
                if (secondSelectedCountry != null) {
                    selectedCountry =
                        countries.find { it.commonName == secondSelectedCountry.commonName }
                            ?: secondSelectedCountry
                    isCountrySelected = true
                    startCountryDetailsActivity()
                }
            }
            if (!isCountrySelected && ::selectedCountry.isInitialized) {
                startCountryDetailsActivity()
            }
        }
        countryViewModel.countryLiveData.observe(this, observer)
    }

    private fun filterCountries(query: String?) {
        val filteredCountries = countryViewModel.countryLiveData.value?.filter { country ->
            country.commonName.contains(query ?: "", ignoreCase = true)
        } ?: emptyList()

        countriesForComparisonFiltered = filteredCountries.filter { it != initialCountry }
        adapter.clear()
        adapter.addAll(countriesForComparisonFiltered.map { country -> country.commonName })
    }

    private fun startCountryDetailsActivity() {
        val intent = Intent(this, SelectCountryDetails::class.java)
        intent.putExtra("initialCountry", initialCountry)
        intent.putExtra("selectedCountry", selectedCountry)
        startActivity(intent)
    }
}
