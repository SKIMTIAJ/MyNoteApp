package com.example.mynotes

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mynotes.adapter.NoteViewAdapter
import com.example.mynotes.databinding.FragmentNoteBinding
import com.example.mynotes.utils.SwipeToDelete
import com.example.mynotes.utils.hideKeybord
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private var _binding:FragmentNoteBinding? = null
    private val binding:FragmentNoteBinding
    get() = _binding!!

    val noteviewModel by viewModels<NoteViewModels>()
    private lateinit var recyclerViewAdapter:NoteViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentNoteBinding.inflate(inflater)

        enterTransition = MaterialElevationScale(false).apply {
            duration = 350
        }

        exitTransition = MaterialElevationScale(true).apply {
            duration = 350
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireView().hideKeybord()
        val activity = activity as MainActivity
        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            activity.window.statusBarColor= Color.WHITE
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor=Color.parseColor("#9E9D9D")
        }
        binding.addNoteFab.setOnClickListener{
            binding.appBarLayout.visibility = View.INVISIBLE
            findNavController().navigate(R.id.action_noteFragment_to_updateFragment)
        }

        recyclerViewDisplay()
        swipeToDelete(binding.recyclerViewNote)

        binding.search.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.noData.isVisible = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               if (s.toString().isNotEmpty()){
                   val text = s.toString()
                   val query = "%$text%"
                   if (query.isNotEmpty()){
                       noteviewModel.getSearchData(query)
                       noteviewModel.searchNoteLiveData.observe(this@NoteFragment,{
                           binding.noData.isVisible =it.isEmpty()
                           recyclerViewAdapter.submitList(it)
                       })
                   }else{
                       observeDataChanges()
                   }
               }else{
                   observeDataChanges()
               }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding.search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                v.clearFocus()
                requireView().hideKeybord()
            }
            return@setOnEditorActionListener true
        }

        binding.recyclerViewNote.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            when{
                scrollY>scrollX -> {
                    binding.addNoteFab.isVisible = false
                }
                scrollX == scrollY ->{
                    binding.addNoteFab.isVisible = true
                }
                else->{
                    binding.addNoteFab.isVisible = true
                }
            }
        }

    }

    private fun swipeToDelete(recyclerViewNote: RecyclerView) {
        val swipeToDeleteCallBack = object :SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = recyclerViewAdapter.currentList[position]
                var actionBtnTappped = false
                noteviewModel.deleteNote(note)

                binding.search.apply {
                    hideKeybord()
                    clearFocus()
                }
                if (binding.search.text.toString().isEmpty()){
                    observeDataChanges()
                }
                val snackBar = Snackbar.make(
                    requireView(),"Note Deleted",Snackbar.LENGTH_LONG
                ).addCallback(object:BaseTransientBottomBar.BaseCallback<Snackbar>(){
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        transientBottomBar?.setAction("UNDO"){
                            noteviewModel.saveNote(note)
                        }
                        super.onShown(transientBottomBar)
                    }
                }).apply {
                    animationMode = Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.addNoteFab)
                }
                snackBar.setActionTextColor(
                    ContextCompat.getColor(requireContext(), R.color.yellowOrange)
                )
                snackBar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerViewNote)
    }

    private fun observeDataChanges() {
        noteviewModel.allNoteLiveData.observe(viewLifecycleOwner){
            binding.noData.isVisible =it.isEmpty()
            recyclerViewAdapter.submitList(it)
        }
    }

    private fun recyclerViewDisplay() {
        noteviewModel.getAllData()
        when(resources.configuration.orientation){
            Configuration.ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecyclerView(3)
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {
        binding.recyclerViewNote.apply {
            layoutManager = StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            recyclerViewAdapter = NoteViewAdapter()
            recyclerViewAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = recyclerViewAdapter

            postponeEnterTransition(300L, TimeUnit.MICROSECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }

        observeDataChanges()
    }


}