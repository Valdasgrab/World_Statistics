package lt.vgrabauskas.worldstatistics.secondactivity


import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import lt.vgrabauskas.worldstatistics.R
import lt.vgrabauskas.worldstatistics.mainactivity.CountryViewModel
import lt.vgrabauskas.worldstatistics.mainactivity.MainActivity
import lt.vgrabauskas.worldstatistics.repository.Country

class SelectCountryActivity : AppCompatActivity() {

    private lateinit var initialCountry: Country
    private lateinit var selectedCountry: Country
    private lateinit var countryViewModel: CountryViewModel
    private lateinit var countriesForComparisonFiltered: List<Country>
    private lateinit var searchView: SearchView
    private lateinit var adapter: ArrayAdapter<String>
    private var isCountrySelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_country)

        initialCountry = intent.getParcelableExtra("selectedCountry")!!

        countryViewModel = ViewModelProvider(this).get(CountryViewModel::class.java)
        countryViewModel.fetchCountries()

        val countriesListView: ListView = findViewById(R.id.countriesListViewComparison)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        countriesListView.adapter = adapter

        searchView = findViewById(R.id.searchView)
        searchView.queryHint = "Search countries"
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
            countriesForComparisonFiltered =
                countries.filter { it != initialCountry }
            adapter.clear()
            adapter.addAll(countriesForComparisonFiltered.map { country -> country.commonName })
            countriesListView.setOnItemClickListener { _, _, position, _ ->
                val secondSelectedCountry = countriesForComparisonFiltered.getOrNull(position)
                if (secondSelectedCountry != null) {
                    selectedCountry =
                        countries.find { it.commonName == secondSelectedCountry.commonName }
                            ?: secondSelectedCountry
                    isCountrySelected = true
                    updateCountryViews()
                }
            }
            if (!isCountrySelected) {
                updateCountryViews()
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

    private fun updateCountryViews() {
        if (!this::selectedCountry.isInitialized) {
            return
        }
        setContentView(R.layout.country_comparison)
        val initialCountryNameTextView: TextView = findViewById(R.id.initialCountryNameTextView)
        val initialCountryDetailsTextView: TextView =
            findViewById(R.id.initialCountryDetailsTextView)
        val initialCountryPopulationTextView: TextView =
            findViewById(R.id.initialCountryPopulationTextView)

        initialCountryNameTextView.text = initialCountry.commonName
        initialCountryDetailsTextView.text = initialCountry.formattedCapital
        initialCountryPopulationTextView.text = initialCountry.population.toString()

        val secondCountryNameTextView: TextView = findViewById(R.id.secondCountryNameTextView)
        val secondCountryDetailsTextView: TextView = findViewById(R.id.secondCountryDetailsTextView)
        val secondCountryPopulationTextView: TextView =
            findViewById(R.id.secondCountryPopulationTextView)

        secondCountryNameTextView.text = selectedCountry.commonName
        secondCountryDetailsTextView.text = selectedCountry.formattedCapital
        secondCountryPopulationTextView.text = selectedCountry.population.toString()

        findViewById<Button>(R.id.backToMainButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        countryViewModel.countryLiveData.removeObservers(this)
        super.onDestroy()
    }
}
