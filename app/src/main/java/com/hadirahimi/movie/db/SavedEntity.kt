package com.hadirahimi.movie.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hadirahimi.movie.utils.Constants.SAVED_TABLE

@Entity(tableName = SAVED_TABLE)
data class SavedEntity(
    @PrimaryKey()
    var server_id:Int = 0,
    var mode : String = "",
    var image_url : String = ""
)