package com.example.mynotes.repositories

import androidx.lifecycle.LiveData
import androidx.room.RoomDatabase
import com.example.mynotes.db.NoteDao
import com.example.mynotes.db.NoteDataBase
import com.example.mynotes.models.NoteData
import javax.inject.Inject

class NoteRepository @Inject constructor(private val roomDatabase:NoteDataBase) {

    suspend fun saveDataLocaly(noteData: NoteData){
        roomDatabase.noteDao().saveInsert(noteData)
    }

    suspend fun getAllData():List<NoteData>{
        return roomDatabase.noteDao().getData()
    }

    suspend fun searchData(query:String):List<NoteData>{
        return roomDatabase.noteDao().serachNote(query)
    }

    suspend fun deleteData(noteData: NoteData){
        return roomDatabase.noteDao().deleteData(noteData)
    }

    suspend fun updateData(noteData: NoteData){
        return roomDatabase.noteDao().updateData(noteData)
    }

}