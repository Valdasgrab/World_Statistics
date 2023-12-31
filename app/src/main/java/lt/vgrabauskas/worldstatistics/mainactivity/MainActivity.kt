package lt.vgrabauskas.worldstatistics.mainactivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import lt.vgrabauskas.worldstatistics.CountryAdapter
import lt.vgrabauskas.worldstatistics.databinding.ActivityMainBinding
import lt.vgrabauskas.worldstatistics.repository.CountryRepository

class MainActivity : AppCompatActivity() {
    private val countryRepository: CountryRepository = CountryRepository.instance
    private lateinit var countryViewModel: CountryViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var countryAdapter: CountryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        countryViewModel = CountryViewModel(countryRepository)
        setupViews()
        setupRecyclerView()
        setupSearchView()
        countryViewModel.fetchCountries()
        observeFetchingLiveData()
        observeFilteredCountries()
    }

    private fun observeFetchingLiveData() {
        countryViewModel.fetchingLiveData.observe(this) { isFetching ->
            binding.swipeRefreshLayout.isRefreshing = isFetching
        }
    }

    private fun setupViews() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
            binding.searchView.requestFocus()
        }
    }

    private fun setupRecyclerView() {
        countryAdapter = CountryAdapter { selectedCountry ->
            val intent = Intent(this, CountryDetails::class.java)
            intent.putExtra("country", selectedCountry)
            startActivity(intent)
        }
        binding.countriesRecyclerView.adapter = countryAdapter
        binding.countriesRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.swipeRefreshLayout.setOnRefreshListener {
            countryViewModel.fetchCountries()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        countryViewModel.filteredCountryLiveData.observe(this) { filteredCountries ->
            filteredCountries?.let {
                countryAdapter.updateData(it)
            }
        }
    }
}
