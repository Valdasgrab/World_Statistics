package lt.vgrabauskas.worldstatistics.secondactivity

import android.os.Bundle
import android.widget.TextView
import lt.vgrabauskas.worldstatistics.ActivityLifecycles
import lt.vgrabauskas.worldstatistics.R
import lt.vgrabauskas.worldstatistics.repository.Country

class CountryDetails: ActivityLifecycles() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.country_details)

        val selectedCountry = intent.getParcelableExtra<Country>("country")

        val countryNameTextView: TextView = findViewById(R.id.countryNameTextView)
        val countryDetailsTextView: TextView = findViewById(R.id.countryDetailsTextView)

        selectedCountry?.let {
            countryNameTextView.text = it.name
            countryDetailsTextView.text = it.details
        }
    }
}