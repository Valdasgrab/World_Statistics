package lt.vgrabauskas.worldstatistics.repository

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Country(
    @SerializedName("name") val countryName: CountryName?,
    @SerializedName("capital") val capital: List<String>?,
    @SerializedName("population") val population: Long
) : Parcelable {
    val commonName: String
        get() = countryName?.common ?: ""

    val formattedCapital: String
        get() = capital?.joinToString(", ") ?: ""

    override fun toString(): String {
        return commonName
    }
}

@Parcelize
data class CountryName(
    @SerializedName("common") val common: String?
) : Parcelable