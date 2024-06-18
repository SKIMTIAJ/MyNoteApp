package com.example.mynotes.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mynotes.db.NoteDao
import com.example.mynotes.db.NoteDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NoteModule {

    @Provides
    @Singleton
    fun provideNoteDao(noteDataBase: NoteDataBase):NoteDao{
        return noteDataBase.noteDao()
    }

    @Provides
    @Singleton
    fun RoomDatabaseProvider(@ApplicationContext context: Context):NoteDataBase{
        return Room.databaseBuilder(context,NoteDataBase::class.java,"NoteDB").build()
    }

}