package com.example.mynotes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.NoteFragment
import com.example.mynotes.NoteFragmentDirections
import com.example.mynotes.R
import com.example.mynotes.UpdateFragmentDirections
import com.example.mynotes.databinding.NoteItemBinding
import com.example.mynotes.models.NoteData
import com.example.mynotes.utils.hideKeybord
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak

class NoteViewAdapter: ListAdapter<NoteData, NoteViewAdapter.NoteAdapterViewholder>(DiffUtillCallBack()) {

    inner class NoteAdapterViewholder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val contentBinding = NoteItemBinding.bind(itemView)
        val title:MaterialTextView = contentBinding.title
        val content: TextView = contentBinding.note
        val date:MaterialTextView = contentBinding.dateTime
        val parentLayout:MaterialCardView = contentBinding.cardParentLayout
        val markWon = Markwon.builder(itemView.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(itemView.context))
            .usePlugin(object :AbstractMarkwonPlugin(){
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(
                        SoftLineBreak::class.java
                    ){visitor, _ ->visitor.forceNewLine()}
                }
            }).build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapterViewholder {
        //val binding = NoteItemBinding.inflate(parent,)
        return NoteAdapterViewholder(LayoutInflater.from(parent.context).inflate(R.layout.note_item,parent,false))
    }

    override fun onBindViewHolder(holder: NoteAdapterViewholder, position: Int) {
        getItem(position).let {note->
            holder.apply {
                parentLayout.transitionName = "recyclerView_${note.id}"
                title.text =note.title
                markWon.setMarkdown(content,note.note)
                date.text = note.date

                itemView.setOnClickListener{
                    val action = NoteFragmentDirections.actionNoteFragmentToUpdateFragment(note)
                    val extras = FragmentNavigatorExtras(parentLayout to "recyclerView_${note.id}")
                    it.hideKeybord()
                    Navigation.findNavController(it).navigate(action,extras)
                }

                content.setOnClickListener{
                    val action = NoteFragmentDirections.actionNoteFragmentToUpdateFragment(note)
                    val extras = FragmentNavigatorExtras(parentLayout to "recyclerView_${note.id}")
                    it.hideKeybord()
                    Navigation.findNavController(it).navigate(action,extras)
                }

            }
        }
    }

}