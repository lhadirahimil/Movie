package com.hadirahimi.movie.models.home


import com.google.gson.annotations.SerializedName

data class ResponseGenre(
    @SerializedName("genres")
    var genres: List<Genre?>? = emptyList()
) {
    data class Genre(
        @SerializedName("id")
        var id: Int=-1,
        @SerializedName("name")
        val name: String = ""
    )
}