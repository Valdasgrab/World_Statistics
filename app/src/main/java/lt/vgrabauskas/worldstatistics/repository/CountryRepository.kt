package lt.vgrabauskas.worldstatistics.repository

class CountryRepository {

    var countries = mutableListOf<Country>()

    fun addDummyListOfCountries() {
        countries.addAll(generateListOfCountries())
    }
    private fun generateListOfCountries(): List<Country> {
        val list = mutableListOf<Country>()

        val lithuania = Country(
            id = 1,
            name = "Lithuania",
            details = "Lithuania is a country in the Baltic region of Europe."
        )

        val latvia = Country(
            id = 2,
            name = "Latvia",
            details = "Latvia is a country in the Baltic region of Northern Europe."
        )

        val estonia = Country(
            id = 3,
            name = "Estonia",
            details = "Estonia is a country on the eastern coast of the Baltic Sea."
        )
        list.add(lithuania)
        list.add(latvia)
        list.add(estonia)
        return list
    }

    companion object {
        val instance: CountryRepository = CountryRepository()
    }
}