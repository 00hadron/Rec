package ru.hadron.rec.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.mikephil.charting.data.LineData
import ru.hadron.rec.db.model.Record

@Dao
interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: Record)

    @Delete
    suspend fun deleteRecord(record: Record)

    @Query("SELECT * FROM record_table ORDER BY timestamp DESC")
    fun getAllRecordsSortedByDate(): LiveData<List<Record>>
}