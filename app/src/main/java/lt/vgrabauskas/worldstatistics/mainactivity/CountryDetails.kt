package lt.vgrabauskas.worldstatistics.mainactivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import lt.vgrabauskas.worldstatistics.R
import lt.vgrabauskas.worldstatistics.databinding.CountryDetailsBinding
import lt.vgrabauskas.worldstatistics.repository.Country
import lt.vgrabauskas.worldstatistics.secondactivity.SelectCountryActivity

class CountryDetails : AppCompatActivity() {
    private lateinit var binding: CountryDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CountryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedCountry = intent.getParcelableExtra<Country>("country")

        selectedCountry?.let {
            binding.countryNameTextView.text = it.commonName
            binding.countryCapitalTextView.text =
                getString(R.string.capital_format, it.formattedCapital)
            binding.countryPopulationTextView.text =
                getString(R.string.population_format, it.population.toString())
            binding.countryAreaTextView.text = getString(R.string.area_format, it.area.toString())

            val languages = it.languages
            val languagesString = languages?.values?.joinToString(", ")
            binding.countryLanguagesTextView.text =
                getString(R.string.languages_format, languagesString)

            val currencies = it.currencies
            if (!currencies.isNullOrEmpty()) {
                val firstCurrencyCode = currencies.keys.first()
                val firstCurrency = currencies[firstCurrencyCode]
                val currencyName = firstCurrency?.name
                val currencySymbol = firstCurrency?.symbol
                binding.countryCurrencyTextView.text =
                    getString(R.string.currency_format, currencyName, currencySymbol)
            }

            val flagUrl = it.flags?.png
            if (flagUrl != null) {
                Glide.with(this)
                    .load(flagUrl)
                    .into(binding.flagImageView)
            }
            val coatOfArmsUrl = it.coatOfArms?.png
            if (coatOfArmsUrl != null) {
                Glide.with(this)
                    .load(coatOfArmsUrl)
                    .into(binding.coatOfArmsImageView)
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.compareButton.setOnClickListener {
            val intent = Intent(this, SelectCountryActivity::class.java)
            intent.putExtra("selectedCountry", selectedCountry)
            startActivityForResult(intent, REQUEST_COMPARE)
        }
    }

    companion object {
        const val REQUEST_COMPARE = 1
    }
}
