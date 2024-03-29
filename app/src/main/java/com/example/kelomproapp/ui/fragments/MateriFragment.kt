package com.example.kelomproapp.ui.fragments

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
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.example.shopeekwapp.ui.fragments.BaseFragment

class MateriFragment : BaseFragment() {

    private var _binding: FragmentMateriBinding? = null
    private val binding get() = _binding!!

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



    fun getMateriItemList(){
        showProgressDialog(resources.getString(R.string.mohon_tunggu))

        FirestoreClass().getAllMateriList(this)
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
                    TODO("Not yet implemented")
                }
            })

        }else{
            binding.rvMateriList.visibility = View.GONE
            binding.tvTidakAdaMateri.visibility = View.VISIBLE
        }
    }

    private fun searchInFirebase(query: String?) {
        FirestoreClass().searchKelompokList(query, object : FirestoreClass.KelompokSearchListener {
            override fun onSearchComplete(kelompokList: ArrayList<Kelompok>) {


                if (kelompokList.isNotEmpty()) {

                    binding.rvMateriList.visibility = View.VISIBLE
                    binding.tvTidakAdaMateri.visibility = View.GONE


                    val kelompokdAdapter = KelompokItemsAdapter(requireActivity(),kelompokList)
                    binding.rvMateriList.adapter = kelompokdAdapter

                    kelompokdAdapter.setOnClickListener(object : KelompokItemsAdapter.OnClickListener{
                        override fun onClick(position: Int, kelompok: Kelompok) {
                            val intent = Intent(requireContext(), TaskListActivity::class.java)
                            intent.putExtra(Constants.DOCUMENT_ID,kelompok.documentId)
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


}
