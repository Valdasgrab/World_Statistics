package lt.vgrabauskas.worldstatistics.secondactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import lt.vgrabauskas.worldstatistics.databinding.CountryComparisonBinding
import lt.vgrabauskas.worldstatistics.mainactivity.MainActivity
import lt.vgrabauskas.worldstatistics.repository.Country


class SelectCountryDetails : AppCompatActivity() {

    private lateinit var binding: CountryComparisonBinding
    private lateinit var initialCountry: Country
    private lateinit var selectedCountry: Country

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CountryComparisonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialCountry = intent.getParcelableExtra("initialCountry") ?: return
        selectedCountry = intent.getParcelableExtra("selectedCountry") ?: return

        val backButton: Button = binding.backToMainButton
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        updateCountryViews()
    }

    private fun updateCountryViews() {
        if (!this::selectedCountry.isInitialized) {
            return
        }

        setCountryViews(
            binding.initialCountryNameTextView,
            binding.initialCountryDetailsTextView,
            binding.initialCountryPopulationTextView,
            binding.initialCountryAreaTextView,
            binding.initialCountryCurrencyTextView,
            binding.initialCountryLanguageTextView,
            binding.initialFlagImageView,
            binding.initialCoatOfArmsImageView,
            initialCountry
        )

        setCountryViews(
            binding.secondCountryNameTextView,
            binding.secondCountryDetailsTextView,
            binding.secondCountryPopulationTextView,
            binding.secondCountryAreaTextView,
            binding.secondCountryCurrencyTextView,
            binding.secondCountryLanguageTextView,
            binding.secondFlagImageView,
            binding.secondCoatOfArmsImageView,
            selectedCountry
        )
    }

    private fun setCountryViews(
        nameTextView: TextView,
        detailsTextView: TextView,
        populationTextView: TextView,
        areaTextView: TextView,
        currencyTextView: TextView,
        languageTextView: TextView,
        flagImageView: ImageView,
        coatOfArmsImageView: ImageView,
        country: Country
    ) {
        nameTextView.text = country.commonName
        detailsTextView.text = "Capital City: \n" + country.formattedCapital
        populationTextView.text = "Population: \n" + country.population.toString()
        areaTextView.text = "Area: \n" + country.area
        languageTextView.text = "Languages: \n" + getLanguagesString(country.languages)
        setCurrencyTextView(currencyTextView, country.currencies)
        flagsAndCoatOfArms(country, flagImageView, coatOfArmsImageView)
    }

    private fun getLanguagesString(languages: Map<String, String>?): String {
        return languages?.values?.joinToString(", ") ?: ""
    }

    private fun setCurrencyTextView(
        textView: TextView,
        currencies: Map<String, Country.Currency>?
    ) {
        if (!currencies.isNullOrEmpty()) {
            val firstCurrencyCode = currencies.keys.first()
            val firstCurrency = currencies[firstCurrencyCode]
            val currencyName = firstCurrency?.name
            val currencySymbol = firstCurrency?.symbol
            textView.text = "Currency: \n$currencyName \n($currencySymbol)"
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
}
