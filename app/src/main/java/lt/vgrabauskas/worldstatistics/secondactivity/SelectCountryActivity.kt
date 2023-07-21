package lt.vgrabauskas.worldstatistics.secondactivity


import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import lt.vgrabauskas.worldstatistics.R
import lt.vgrabauskas.worldstatistics.mainactivity.CountryViewModel
import lt.vgrabauskas.worldstatistics.repository.Country

class SelectCountryActivity : AppCompatActivity() {

    private lateinit var initialCountry: Country
    private lateinit var countryViewModel: CountryViewModel
    private lateinit var countriesForComparisonFiltered: List<Country>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_country)

        initialCountry = intent.getParcelableExtra("selectedCountry")!!

        countryViewModel = ViewModelProvider(this).get(CountryViewModel::class.java)
        countryViewModel.fetchCountries()

        val countriesListView: ListView = findViewById(R.id.countriesListView)
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
            adapter.addAll(countriesForComparisonFiltered.map { country ->  country.name})
            countriesListView.setOnItemClickListener { _, _, position, _ ->
                val selectedCountry = countriesForComparisonFiltered.getOrNull(position)
                if (selectedCountry != null) {
                    val intent = Intent()
                    intent.putExtra("SecondCountry", selectedCountry)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
        countryViewModel.countryLiveData.observe(this, observer)
    }
    override fun onDestroy() {
        countryViewModel.countryLiveData.removeObservers(this)
        super.onDestroy()
    }
}
