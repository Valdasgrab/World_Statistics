package lt.vgrabauskas.worldstatistics.mainactivity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import lt.vgrabauskas.worldstatistics.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val countryViewModel: CountryViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        binding.countriesListView.adapter = adapter
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
                adapter.clear()
                adapter.addAll(countries.map { country -> country.commonName })
            }
        }

        binding.countriesListView.setOnItemClickListener { adapterView, view, position, listener ->
            val selectedCountryName = adapter.getItem(position)
            val selectedCountry =
                countryViewModel.countryLiveData.value?.find { it.commonName == selectedCountryName }
            if (selectedCountry != null) {
                val intent = Intent(this, CountryDetails::class.java)
                intent.putExtra("country", selectedCountry)
                intent.putParcelableArrayListExtra(
                    "allCountries",
                    countryViewModel.countryLiveData.value?.let { ArrayList(it) }
                )
                startActivity(intent)
            }
        }
    }

    private fun filterCountries(query: String?) {
        val filteredCountries = countryViewModel.countryLiveData.value?.filter { country ->
            country.commonName.contains(query ?: "", ignoreCase = true)
        } ?: emptyList()
        adapter.clear()
        adapter.addAll(filteredCountries.map { country -> country.commonName })
    }
}
