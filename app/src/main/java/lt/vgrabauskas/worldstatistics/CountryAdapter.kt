package lt.vgrabauskas.worldstatistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import lt.vgrabauskas.worldstatistics.databinding.ListItemCountryBinding
import lt.vgrabauskas.worldstatistics.repository.Country

class CountryAdapter(private val onCountryClick: (Country) -> Unit) :
    RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var countries: List<Country> = emptyList()

    class CountryViewHolder(private val binding: ListItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(country: Country) {
            binding.countryListNameTextView.text = country.commonName
        }
    }

    fun updateData(newCountries: List<Country>) {
        countries = newCountries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCountryBinding.inflate(inflater, parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]
        holder.bind(country)

        holder.itemView.setOnClickListener {
            val clickedCountry = countries[position]
            onCountryClick(clickedCountry)
        }
    }

    override fun getItemCount(): Int {
        return countries.size
    }
}
