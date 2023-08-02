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
        setupViews()
        setupRecyclerView()
        setupSearchView()
        countryViewModel.initializeFilter()
        observeFilteredCountries()
        observeFetchingLiveData()
        countryViewModel.fetchCountries()
    }

    private fun observeFetchingLiveData() {
        countryViewModel.fetchingLiveData.observe(this) { isFetching ->
            binding.swipeRefreshLayoutComparison.isRefreshing = isFetching
        }
    }

    private fun setupViews() {
        binding.searchViewComparison.queryHint = "Search countries"
        binding.searchViewComparison.setOnClickListener {
            binding.searchViewComparison.isIconified = false
            binding.searchViewComparison.requestFocus()
        }
    }

    private fun setupRecyclerView() {
        countryAdapter = CountryAdapter { clickedCountry ->
            selectedCountry = clickedCountry
            isCountrySelected = true
            startCountryDetailsActivity()
        }
        binding.countriesRecyclerViewComparison.adapter = countryAdapter
        binding.countriesRecyclerViewComparison.layoutManager = LinearLayoutManager(this)
        binding.swipeRefreshLayoutComparison.setOnRefreshListener {
            countryViewModel.fetchCountries()
        }
    }

    private fun setupSearchView() {
        binding.searchViewComparison.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                countryViewModel.filterCountries(newText)
                return true
            }
        })
    }

    private fun observeFilteredCountries() {
        val observer = Observer<List<Country>> { countries ->
            val countriesForComparisonFiltered = countries.filter { it != initialCountry }
            countryAdapter.updateData(countriesForComparisonFiltered)

            if (!isCountrySelected && ::selectedCountry.isInitialized) {
                startCountryDetailsActivity()
            }
        }
        countryViewModel.filteredCountryLiveData.observe(this, observer)
    }

    private fun startCountryDetailsActivity() {
        val intent = Intent(this, SelectCountryDetails::class.java)
        intent.putExtra("initialCountry", initialCountry)
        intent.putExtra("selectedCountry", selectedCountry)
        startActivity(intent)
    }
}
