package lt.vgrabauskas.worldstatistics.repository

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Country(
    @SerializedName("name") val countryName: CountryName?,
    @SerializedName("capital") val capital: List<String>?,
    @SerializedName("population") val population: Long,
    @SerializedName("area") val area: Double?,
    @SerializedName("currencies") val currencies: Map<String, Currency>?,
    @SerializedName("languages") val languages: Map<String, String>?,
    @SerializedName("flags") val flags: Flags?,
    @SerializedName("coatOfArms") val coatOfArms: CoatOfArms?
) : Parcelable {
    val commonName: String
        get() = countryName?.common ?: ""

    val formattedCapital: String
        get() = capital?.joinToString(", ") ?: ""

    override fun toString(): String {
        return commonName
    }

    @Parcelize
    data class CountryName(
        @SerializedName("common") val common: String?
    ) : Parcelable

    @Parcelize
    data class Currency(
        @SerializedName("name") val name: String?,
        @SerializedName("symbol") val symbol: String?
    ) : Parcelable

    @Parcelize
    data class Flags(
        @SerializedName("png") val png: String?,
        @SerializedName("svg") val svg: String?,
        @SerializedName("alt") val alt: String?
    ) : Parcelable

    @Parcelize
    data class CoatOfArms(
        @SerializedName("png") val png: String?,
        @SerializedName("svg") val svg: String?
    ) : Parcelable
}
