package ru.hadron.rec.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record_table")
data class Record (
   // val id: Int? = null,
    val title: String = "",
    var timestamp: Long = 0L
) {
    @PrimaryKey
        (autoGenerate = true)
    var id: Int? = null
}