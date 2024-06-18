package com.example.mynotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mynotes.repositories.NoteRepository

class NoteActivityViewModelFactory(private val noteRepository:NoteRepository):
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModels(noteRepository) as T
    }
}