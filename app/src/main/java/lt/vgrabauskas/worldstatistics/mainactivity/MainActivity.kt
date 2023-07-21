package lt.vgrabauskas.worldstatistics.mainactivity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import lt.vgrabauskas.worldstatistics.R
import lt.vgrabauskas.worldstatistics.secondactivity.CountryDetails

class MainActivity : AppCompatActivity() {

    private val countryViewModel: CountryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val countriesListView: ListView = findViewById(R.id.countriesListView)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        countriesListView.adapter = adapter

        countryViewModel.fetchCountries()

        countryViewModel.countryLiveData.observe(this, Observer { countries ->
            countries?.let {
                adapter.clear()
                adapter.addAll(countries.map { country -> country.name })
            }
        })

        countriesListView.setOnItemClickListener { adapterView, view, position, listener ->
            val selectedCountry = countryViewModel.countryLiveData.value?.get(position)
            if (selectedCountry != null) {
                val intent = Intent(this, CountryDetails::class.java)
                intent.putExtra("country", selectedCountry)
                startActivity(intent)
            }

        }
    }
}
