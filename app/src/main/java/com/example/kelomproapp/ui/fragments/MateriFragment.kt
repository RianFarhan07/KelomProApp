package com.example.kelomproapp.ui.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.KelompokItemsAdapter
import com.example.kelomproapp.adapter.MateriItemsAdapter
import com.example.kelomproapp.databinding.FragmentMateriBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Materi
import com.example.kelomproapp.ui.activities.CreateMateriActivity
import com.example.kelomproapp.ui.activities.TaskListActivity
import com.example.kelomproapp.utils.Constants
import com.example.kelomproapp.utils.SwipeToDeleteCallback
import com.example.kelomproapp.utils.SwipeToEditCallback
import com.example.shopeekwapp.ui.fragments.BaseFragment

class MateriFragment : BaseFragment() {

    private var _binding: FragmentMateriBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val EDIT_MATERI_REQUEST_CODE = 100
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMateriBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.setOnClickListener {
            // Set focus to the SearchView to show keyboard
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchInFirebase(newText)
                return false
            }
        })

        binding.fabCreateMateri.setOnClickListener{
            val intent = Intent(requireContext(), CreateMateriActivity::class.java)
            startActivity(intent)
        }



    }

    override fun onResume() {
        super.onResume()
        getMateriItemList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_MATERI_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            FirestoreClass().getAllMateriListFragment(this)
        }
    }


    fun getMateriItemList(){
        showProgressDialog(resources.getString(R.string.mohon_tunggu))
        FirestoreClass().getAllMateriListFragment(this)
    }

    fun successMateriItemsList(materiItemsList: ArrayList<Materi>){
        hideProgressDialog()

        if (materiItemsList.size > 0){
            binding.rvMateriList.visibility = View.VISIBLE
            binding.tvTidakAdaMateri.visibility = View.GONE

            binding.rvMateriList.layoutManager = LinearLayoutManager(activity)
            binding.rvMateriList.setHasFixedSize(true)

            val materiAdapter = MateriItemsAdapter(requireActivity(),materiItemsList)
            binding.rvMateriList.adapter = materiAdapter

            materiAdapter.setOnClickListener(object : MateriItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Materi) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(model.url)
                    startActivity(intent)
                }
            })

            val editSwipeHandler = object : SwipeToEditCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = binding.rvMateriList.adapter as MateriItemsAdapter
                    adapter.notifyEditItem(
                        this@MateriFragment,
                        viewHolder.adapterPosition
                    )
                }
            }



            val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
            editItemTouchHelper.attachToRecyclerView(binding.rvMateriList)

            val deleteSwipeHandler = object: SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    showProgressDialog(resources.getString(R.string.mohon_tunggu))
                    FirestoreClass().deleteMateri(this@MateriFragment,
                        materiItemsList[viewHolder.adapterPosition].id)
                }
            }

            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(binding.rvMateriList)

        }else{
            binding.rvMateriList.visibility = View.GONE
            binding.tvTidakAdaMateri.visibility = View.VISIBLE
        }
    }

    private fun searchInFirebase(query: String?) {
        FirestoreClass().searchMateriList(query, object : FirestoreClass.MateriSearchListener {
            override fun onSearchComplete(materiList: ArrayList<Materi>) {


                if (materiList.isNotEmpty()) {

                    binding.rvMateriList.visibility = View.VISIBLE
                    binding.tvTidakAdaMateri.visibility = View.GONE


                    val materiAdapter = MateriItemsAdapter(requireActivity(),materiList)
                    binding.rvMateriList.adapter = materiAdapter
                    //TODO NOTIFY SEARCH ITEM
//                    materiAdapter.notifySearchItem(this,.)

                    materiAdapter.setOnClickListener(object : MateriItemsAdapter.OnClickListener{
                        override fun onClick(position: Int, model: Materi) {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(model.url)
                            startActivity(intent)
                        }
                    })
                } else {

                    binding.rvMateriList.visibility = View.GONE
                    binding.tvTidakAdaMateri.visibility = View.VISIBLE
                }
            }
        })
    }

    fun materiDeleteSuccess(){
        hideProgressDialog()
        Toast.makeText(context,"Berhasil menghapus materi",
            Toast.LENGTH_LONG).show()

        getMateriItemList()
    }


}
