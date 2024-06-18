package com.example.mynotes.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mynotes.models.NoteData

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveInsert(noteData: NoteData)

    @Update
    suspend fun updateData(noteData: NoteData)

    @Delete
    suspend fun deleteData(noteData: NoteData)

    @Query("SELECT*FROM NoteData ORDER BY id DESC")
    suspend fun getData():List<NoteData>

    @Query("SELECT * FROM NoteData WHERE title LIKE:query or note LIKE:query OR date LIKE:query ORDER BY id DESC")
    suspend fun serachNote(query:String):List<NoteData>

}