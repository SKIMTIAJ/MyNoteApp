package com.example.mynotes.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.mynotes.models.NoteData

class DiffUtillCallBack():DiffUtil.ItemCallback<NoteData>() {
    override fun areItemsTheSame(oldItem: NoteData, newItem: NoteData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NoteData, newItem: NoteData): Boolean {
        return oldItem == newItem
    }
}