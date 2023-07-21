package lt.vgrabauskas.worldstatistics.secondactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import lt.vgrabauskas.worldstatistics.ActivityLifecycles
import lt.vgrabauskas.worldstatistics.R
import lt.vgrabauskas.worldstatistics.repository.Country

class CountryDetails : ActivityLifecycles() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.country_details)

        val selectedCountry = intent.getParcelableExtra<Country>("country")

        selectedCountry?.let {
            findViewById<TextView>(R.id.countryNameTextView).text = it.name
            findViewById<TextView>(R.id.countryDetailsTextView).text = it.details
        }

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.compareButton).setOnClickListener {
            val intent = Intent(this, SelectCountryActivity::class.java)
            intent.putExtra("selectedCountry", selectedCountry)
            startActivityForResult(intent, REQUEST_COMPARE)
        }
    }


    companion object {
        const val REQUEST_COMPARE = 1
    }
}
