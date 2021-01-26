package ru.hadron.rec.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record_table")
data class Record (
   // val id: Int? = null,
    val title: String = "", //file name
    val recordFilePath: String = "",
    val recordLengthInMs: Long = 0L,
    var timestamp: Long = 0L //date time of the record
) {
    @PrimaryKey
        (autoGenerate = true)
    var id: Int? = null
}