package lt.vgrabauskas.worldstatistics.secondactivity


import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import lt.vgrabauskas.worldstatistics.CountryAdapter
import lt.vgrabauskas.worldstatistics.databinding.ActivitySelectCountryBinding
import lt.vgrabauskas.worldstatistics.repository.Country

class SelectCountryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectCountryBinding
    private lateinit var initialCountry: Country
    private lateinit var selectedCountry: Country
    private lateinit var countryAdapter: CountryAdapter
    private val countryViewModel: SelectCountryViewModel by viewModels()

    private var isCountrySelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialCountry = intent.getParcelableExtra("selectedCountry")!!

        setupRecyclerView()

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

        val observer = Observer<List<Country>> { countries ->
            val countriesForComparisonFiltered = countries.filter { it != initialCountry }
            countryAdapter.updateData(countriesForComparisonFiltered)

            if (!isCountrySelected && ::selectedCountry.isInitialized) {
                startCountryDetailsActivity()
            }
        }
        countryViewModel.countryLiveData.observe(this, observer)
        countryViewModel.fetchCountries()
    }

    private fun setupRecyclerView() {
        countryAdapter = CountryAdapter { clickedCountry ->
            selectedCountry = clickedCountry
            isCountrySelected = true
            startCountryDetailsActivity()
        }

        binding.countriesRecyclerViewComparison.adapter = countryAdapter
        binding.countriesRecyclerViewComparison.layoutManager = LinearLayoutManager(this)
    }

    private fun filterCountries(query: String?) {
        val filteredCountries = countryViewModel.countryLiveData.value?.filter { country ->
            country.commonName.contains(query ?: "", ignoreCase = true)
        } ?: emptyList()

        val countriesForComparisonFiltered = filteredCountries.filter { it != initialCountry }
        countryAdapter.updateData(countriesForComparisonFiltered)
    }

    private fun startCountryDetailsActivity() {
        val intent = Intent(this, SelectCountryDetails::class.java)
        intent.putExtra("initialCountry", initialCountry)
        intent.putExtra("selectedCountry", selectedCountry)
        startActivity(intent)
    }
}
