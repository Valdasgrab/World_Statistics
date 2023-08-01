package lt.vgrabauskas.worldstatistics.secondactivity


import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import lt.vgrabauskas.worldstatistics.R
import lt.vgrabauskas.worldstatistics.databinding.ActivitySelectCountryBinding
import lt.vgrabauskas.worldstatistics.databinding.CountryComparisonBinding
import lt.vgrabauskas.worldstatistics.mainactivity.CountryViewModel
import lt.vgrabauskas.worldstatistics.mainactivity.MainActivity
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
                    updateCountryViews()
                }
            }
            if (!isCountrySelected && ::selectedCountry.isInitialized) {
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
        setInitialCountryViews(initialCountry)
        setSecondCountryViews(selectedCountry)

        findViewById<Button>(R.id.backToMainButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun setInitialCountryViews(country: Country) {
        val initialCountryNameTextView: TextView = findViewById(R.id.initialCountryNameTextView)
        val initialCountryDetailsTextView: TextView =
            findViewById(R.id.initialCountryDetailsTextView)
        val initialCountryPopulationTextView: TextView =
            findViewById(R.id.initialCountryPopulationTextView)
        val initialCountryAreaTextView: TextView = findViewById(R.id.initialCountryAreaTextView)
        val initialCountryLanguageTextView: TextView =
            findViewById(R.id.initialCountryLanguageTextView)

        initialCountryNameTextView.text = country.commonName
        initialCountryDetailsTextView.text = "Capital City: \n" + country.formattedCapital
        initialCountryPopulationTextView.text = "Population: \n" + country.population.toString()
        initialCountryAreaTextView.text = "Area: \n" + country.area
        initialCountryLanguageTextView.text =
            "Languages: \n" + getLanguagesString(country.languages)
        setCurrencyTextView(R.id.initialCountryCurrencyTextView, country.currencies)
        flagsAndCoatOfArms(
            country,
            findViewById(R.id.initialFlagImageView),
            findViewById(R.id.initialCoatOfArmsImageView)
        )
    }

    private fun setSecondCountryViews(country: Country) {
        val secondCountryNameTextView: TextView = findViewById(R.id.secondCountryNameTextView)
        val secondCountryDetailsTextView: TextView = findViewById(R.id.secondCountryDetailsTextView)
        val secondCountryPopulationTextView: TextView =
            findViewById(R.id.secondCountryPopulationTextView)
        val secondCountryAreaTextView: TextView = findViewById(R.id.secondCountryAreaTextView)
        val secondCountryLanguageTextView: TextView =
            findViewById(R.id.secondCountryLanguageTextView)

        secondCountryNameTextView.text = country.commonName
        secondCountryDetailsTextView.text = "Capital City: \n" + country.formattedCapital
        secondCountryPopulationTextView.text = "Population: \n" + country.population.toString()
        secondCountryAreaTextView.text = "Area: \n" + country.area
        secondCountryLanguageTextView.text = "Languages: \n" + getLanguagesString(country.languages)
        setCurrencyTextView(R.id.secondCountryCurrencyTextView, country.currencies)
        flagsAndCoatOfArms(
            country,
            findViewById(R.id.secondFlagImageView),
            findViewById(R.id.secondCoatOfArmsImageView)
        )
    }

    private fun getLanguagesString(languages: Map<String, String>?): String {
        return languages?.values?.joinToString(", ") ?: ""
    }

    private fun setCurrencyTextView(textViewId: Int, currencies: Map<String, Country.Currency>?) {
        if (!currencies.isNullOrEmpty()) {
            val firstCurrencyCode = currencies.keys.first()
            val firstCurrency = currencies[firstCurrencyCode]
            val currencyName = firstCurrency?.name
            val currencySymbol = firstCurrency?.symbol
            findViewById<TextView>(textViewId).text =
                "Currency: \n$currencyName \n($currencySymbol)"
        }
    }

    private fun flagsAndCoatOfArms(
        country: Country,
        flagImageView: ImageView,
        coatOfArmsImageView: ImageView
    ) {
        val flagUrl = country.flags?.png
        if (flagUrl != null) {
            Glide.with(this)
                .load(flagUrl)
                .into(flagImageView)
        }
        val coatOfArmsUrl = country.coatOfArms?.png
        if (coatOfArmsUrl != null) {
            Glide.with(this)
                .load(coatOfArmsUrl)
                .into(coatOfArmsImageView)
        }
    }

    override fun onDestroy() {
        countryViewModel.countryLiveData.removeObservers(this)
        super.onDestroy()
    }
}

