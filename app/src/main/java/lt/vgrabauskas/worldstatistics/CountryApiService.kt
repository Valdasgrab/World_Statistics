package lt.vgrabauskas.worldstatistics

import lt.vgrabauskas.worldstatistics.repository.Country
import retrofit2.http.GET

interface CountryApiService {
    @GET("v3.1/all")
    suspend fun getCountries(): List<Country>
}
