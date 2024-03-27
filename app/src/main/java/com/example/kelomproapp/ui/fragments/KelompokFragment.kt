package com.example.kelomproapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.KelompokItemsAdapter
import com.example.kelomproapp.databinding.FragmentKelompokBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.ui.activities.IntroActivity
import com.example.kelomproapp.ui.activities.TaskListActivity
import com.example.kelomproapp.utils.Constants
import com.example.shopeekwapp.ui.fragments.BaseFragment
import com.google.firebase.auth.FirebaseAuth

class KelompokFragment : BaseFragment() {

    private var _binding: FragmentKelompokBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKelompokBinding.inflate(inflater, container, false)
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

    }

    override fun onResume() {
        super.onResume()
        getKelompokItemList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun getKelompokItemList(){
        showProgressDialog(resources.getString(R.string.mohon_tunggu))

        FirestoreClass().getAllKelompokList(this)
    }

    fun successKelompokItemsList(kelompokItemsList: ArrayList<Kelompok>){
        hideProgressDialog()

        if (kelompokItemsList.size > 0){
            binding.rvKelompokList.visibility = View.VISIBLE
            binding.tvTidakAdaKelompok.visibility = View.GONE

            binding.rvKelompokList.layoutManager = LinearLayoutManager(activity)
            binding.rvKelompokList.setHasFixedSize(true)

            val kelompokdAdapter = KelompokItemsAdapter(requireActivity(),kelompokItemsList)
            binding.rvKelompokList.adapter = kelompokdAdapter

            kelompokdAdapter.setOnClickListener(object : KelompokItemsAdapter.OnClickListener{
                override fun onClick(position: Int, kelompok: Kelompok) {
                    val intent = Intent(context,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,kelompok.documentId)
                    startActivity(intent)
                }
            })

        }else{
            binding.rvKelompokList.visibility = View.GONE
            binding.tvTidakAdaKelompok.visibility = View.VISIBLE
        }
    }

    private fun searchInFirebase(query: String?) {
        FirestoreClass().searchKelompokList(query, object : FirestoreClass.KelompokSearchListener {
            override fun onSearchComplete(kelompokList: ArrayList<Kelompok>) {


                if (kelompokList.isNotEmpty()) {

                    binding.rvKelompokList.visibility = View.VISIBLE
                    binding.tvTidakAdaKelompok.visibility = View.GONE


                    val kelompokdAdapter = KelompokItemsAdapter(requireActivity(),kelompokList)
                    binding.rvKelompokList.adapter = kelompokdAdapter

                    kelompokdAdapter.setOnClickListener(object : KelompokItemsAdapter.OnClickListener{
                        override fun onClick(position: Int, kelompok: Kelompok) {
                            val intent = Intent(requireContext(),TaskListActivity::class.java)
                            intent.putExtra(Constants.DOCUMENT_ID,kelompok.documentId)
                            startActivity(intent)
                        }
                    })
                } else {

                    binding.rvKelompokList.visibility = View.GONE
                    binding.tvTidakAdaKelompok.visibility = View.VISIBLE
                }
            }
        })
    }
}