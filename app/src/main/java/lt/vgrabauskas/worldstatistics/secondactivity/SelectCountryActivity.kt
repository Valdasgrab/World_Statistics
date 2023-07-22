package lt.vgrabauskas.worldstatistics.secondactivity


import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    private var isCountrySelected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_country)

        initialCountry = intent.getParcelableExtra("selectedCountry")!!

        countryViewModel = ViewModelProvider(this).get(CountryViewModel::class.java)
        countryViewModel.fetchCountries()

        val countriesListView: ListView = findViewById(R.id.countriesListViewComparison)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            mutableListOf<String>()
        )

        countriesListView.adapter = adapter

        countryViewModel.countryLiveData.removeObservers(this)

        val observer = Observer<List<Country>> { countries ->
            countriesForComparisonFiltered =
                countries?.filter { it != initialCountry } ?: emptyList()
            adapter.clear()
            adapter.addAll(countriesForComparisonFiltered.map { country -> country.name })
            countriesListView.setOnItemClickListener { _, _, position, _ ->
                val secondSelectedCountry = countriesForComparisonFiltered.getOrNull(position)
                if (secondSelectedCountry != null) {
                    selectedCountry = secondSelectedCountry
                    isCountrySelected = true
                    updateCountryViews()
                }
            }
            if (!isCountrySelected) {
                // If no country is selected yet, display the initial country details by default
                updateCountryViews()
            }
        }

        countryViewModel.countryLiveData.observe(this, observer)


    }

    private fun updateCountryViews() {
        if (!this::selectedCountry.isInitialized) {
            return
        }
        setContentView(R.layout.country_comparison)
        // Update the initial country views
        val initialCountryNameTextView: TextView = findViewById(R.id.initialCountryNameTextView)
        val initialCountryDetailsTextView: TextView = findViewById(R.id.initialCountryDetailsTextView)

        initialCountryNameTextView.text = initialCountry.name
        initialCountryDetailsTextView.text = initialCountry.details

        // Update the second selected country views
        val secondCountryNameTextView: TextView = findViewById(R.id.secondCountryNameTextView)
        val secondCountryDetailsTextView: TextView = findViewById(R.id.secondCountryDetailsTextView)

        secondCountryNameTextView.text = selectedCountry.name
        secondCountryDetailsTextView.text = selectedCountry.details


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
