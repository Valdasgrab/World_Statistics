package lt.vgrabauskas.worldstatistics.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Country(
    private val _id: Int,
    private val _name: String,
    private val _details: String

) : Parcelable