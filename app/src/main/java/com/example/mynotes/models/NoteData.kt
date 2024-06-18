package com.example.mynotes.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class NoteData(
    val title:String,
    val note:String,
    val date:String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
    ): Serializable
