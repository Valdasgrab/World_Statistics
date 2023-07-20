package lt.vgrabauskas.worldstatistics.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Country(
    val id: Int,
    val name: String,
    val details: String

) : Parcelable