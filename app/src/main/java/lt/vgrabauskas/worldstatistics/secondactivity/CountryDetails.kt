package lt.vgrabauskas.worldstatistics.secondactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

import lt.vgrabauskas.worldstatistics.R
import lt.vgrabauskas.worldstatistics.repository.Country

class CountryDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.country_details)

        val selectedCountry = intent.getParcelableExtra<Country>("country")



        selectedCountry?.let {
            findViewById<TextView>(R.id.countryNameTextView).text = it.commonName
            findViewById<TextView>(R.id.countryCapitalTextView).text =
                "Capital: " + selectedCountry?.formattedCapital
            findViewById<TextView>(R.id.countryPopulationTextView).text =
                "Population: " + it.population
            findViewById<TextView>(R.id.countryAreaTextView).text = "Area: " + it.area
            val languages = it.languages
            val languagesString = languages?.values?.joinToString(", ")
            findViewById<TextView>(R.id.countryLanguagesTextView).text =
                "Languages: " + languagesString
            val currencies = it.currencies
            if (!currencies.isNullOrEmpty()) {
                val firstCurrencyCode = currencies.keys.first()
                val firstCurrency = currencies[firstCurrencyCode]
                val currencyName = firstCurrency?.name
                val currencySymbol = firstCurrency?.symbol
                findViewById<TextView>(R.id.countryCurrencyTextView).text =
                    "Currency: $currencyName ($currencySymbol)"
            }
            val flagUrl = it.flags?.png
            if (flagUrl != null) {
                Glide.with(this)
                    .load(flagUrl)
                    .into(findViewById<ImageView>(R.id.flagImageView))
            }
            val coatOfArmsUrl = it.coatOfArms?.png
            if (coatOfArmsUrl != null) {
                Glide.with(this)
                    .load(coatOfArmsUrl)
                    .into(findViewById<ImageView>(R.id.coatOfArmsImageView))
            }
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
