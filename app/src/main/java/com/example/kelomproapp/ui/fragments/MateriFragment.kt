package com.example.kelomproapp.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.MateriItemsAdapter
import com.example.kelomproapp.databinding.FragmentMateriBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Materi
import com.example.kelomproapp.ui.activities.CreateMateriActivity
import com.example.kelomproapp.utils.SwipeToDeleteCallback
import com.example.kelomproapp.utils.SwipeToEditCallback
import com.example.shopeekwapp.ui.fragments.BaseFragment
import java.util.*

class MateriFragment : BaseFragment() {

    private var _binding: FragmentMateriBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdapter: MateriItemsAdapter
    private var materiList = ArrayList<Materi>()

    companion object {
        const val EDIT_MATERI_REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMateriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchInFirebase(newText)
                return true
            }
        })

        binding.fabCreateMateri.setOnClickListener {
            val intent = Intent(requireContext(), CreateMateriActivity::class.java)
            startActivity(intent)
        }

        mAdapter = MateriItemsAdapter(requireActivity(), materiList)
        binding.rvMateriList.layoutManager = LinearLayoutManager(activity)
        binding.rvMateriList.adapter = mAdapter

        val editSwipeHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                mAdapter.notifyEditItem(this@MateriFragment, viewHolder.adapterPosition)
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.rvMateriList)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showProgressDialog(resources.getString(R.string.mohon_tunggu))
                FirestoreClass().deleteMateri(
                    this@MateriFragment,
                    materiList[viewHolder.adapterPosition].id
                )
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.rvMateriList)
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
            getMateriItemList()
        }
    }

    private fun getMateriItemList() {
        FirestoreClass().getAllMateriListFragment(this)
    }

    fun successMateriItemsList(materiItemsList: ArrayList<Materi>) {

        materiList.clear()
        materiList.addAll(materiItemsList)
        mAdapter.notifyDataSetChanged()

        mAdapter.setOnClickListener(object : MateriItemsAdapter.OnClickListener {
            override fun onClick(position: Int, model: Materi) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(model.url)
                startActivity(intent)
            }
        })

        if (materiItemsList.isNotEmpty()) {
            binding.rvMateriList.visibility = View.VISIBLE
            binding.tvTidakAdaMateri.visibility = View.GONE
        } else {
            binding.rvMateriList.visibility = View.GONE
            binding.tvTidakAdaMateri.visibility = View.VISIBLE
        }
    }

    fun materiDeleteSuccess() {
        hideProgressDialog()
        Toast.makeText(context, "Berhasil menghapus materi", Toast.LENGTH_LONG).show()
        getMateriItemList()
    }

    private fun searchInFirebase(query: String?) {
        FirestoreClass().searchMateriList(query, object : FirestoreClass.MateriSearchListener {
            override fun onSearchComplete(materiList: ArrayList<Materi>) {
                successMateriItemsList(materiList)
            }
        })
    }
}
