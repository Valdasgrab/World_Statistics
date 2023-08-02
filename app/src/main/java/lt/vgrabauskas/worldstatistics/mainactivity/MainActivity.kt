package lt.vgrabauskas.worldstatistics.mainactivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import lt.vgrabauskas.worldstatistics.CountryAdapter
import lt.vgrabauskas.worldstatistics.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val countryViewModel: CountryViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var countryAdapter: CountryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        countryAdapter = CountryAdapter { selectedCountry ->
            val intent = Intent(this, CountryDetails::class.java)
            intent.putExtra("country", selectedCountry)
            startActivity(intent)
        }

        binding.countriesRecyclerView.adapter = countryAdapter
        binding.countriesRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
            binding.searchView.requestFocus()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCountries(newText)
                return true
            }
        })

        countryViewModel.fetchCountries()

        countryViewModel.countryLiveData.observe(this) { countries ->
            countries?.let {
                countryAdapter.updateData(it)
            }
        }
    }

    private fun filterCountries(query: String?) {
        val filteredCountries = countryViewModel.countryLiveData.value?.filter { country ->
            country.commonName.contains(query ?: "", ignoreCase = true)
        } ?: emptyList()
        countryAdapter.updateData(filteredCountries)
    }
}
