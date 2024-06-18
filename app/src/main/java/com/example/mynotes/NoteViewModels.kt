package com.example.mynotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.models.NoteData
import com.example.mynotes.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModels @Inject constructor(private val repository:NoteRepository):ViewModel() {

    private var mutableLiveData = MutableLiveData<List<NoteData>>()
    val allNoteLiveData: LiveData<List<NoteData>>
    get() = mutableLiveData

    private var searchMutableLiveData = MutableLiveData<List<NoteData>>()
    val searchNoteLiveData: LiveData<List<NoteData>>
    get() = searchMutableLiveData

    fun saveDataToRoom(title:String, note:String,date:String){
        viewModelScope.launch {
            repository.saveDataLocaly(NoteData(title,note,date))
        }
    }
    fun saveNote(note:NoteData){
        viewModelScope.launch {
            repository.updateData(note)
        }
    }

    fun getAllData(){
        viewModelScope.launch {
            val allData = repository.getAllData()
            if (allData!=null && allData.isNotEmpty()){
                mutableLiveData.postValue(allData)
            }
        }
    }

    fun getSearchData(query:String){
        viewModelScope.launch(Dispatchers.IO) {
            val searchData = repository.searchData(query)
            if (searchData!=null && searchData.isNotEmpty()){
                searchMutableLiveData.postValue(searchData)
            }else{

            }
        }
    }

    fun deleteNote(note:NoteData){
        viewModelScope.launch {
            repository.deleteData(note)
        }
    }
}