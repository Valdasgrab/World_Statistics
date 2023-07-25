package lt.vgrabauskas.worldstatistics.repository

import lt.vgrabauskas.worldstatistics.CountryApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CountryRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://restcountries.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(CountryApiService::class.java)

    suspend fun fetchCountries(): List<Country> {
        return apiService.getCountries()
    }

    companion object {
        val instance: CountryRepository = CountryRepository()
    }
}
