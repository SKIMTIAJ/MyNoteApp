package com.example.mynotes.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mynotes.models.NoteData

@Database(entities = [NoteData::class], version = 1, exportSchema = false)
abstract class NoteDataBase():RoomDatabase() {
    abstract fun noteDao():NoteDao
}