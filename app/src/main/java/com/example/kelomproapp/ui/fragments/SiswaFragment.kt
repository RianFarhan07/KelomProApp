package com.example.kelomproapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.SiswaItemsAdapter
import com.example.kelomproapp.databinding.FragmentSiswaBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Siswa
import com.example.shopeekwapp.ui.fragments.BaseFragment

class SiswaFragment : BaseFragment() {

    private var _binding: FragmentSiswaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSiswaBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
       getSiswaItemList()
    }

    fun getSiswaItemList(){
        showProgressDialog(resources.getString(R.string.mohon_tunggu))

        FirestoreClass().getSiswaListDetails(this)
    }

    fun successSiswaItemsList(siswaItemsList: ArrayList<Siswa>){
        hideProgressDialog()

        if (siswaItemsList.size > 0){
            binding.rvSiswaList.visibility = View.VISIBLE
            binding.tvTidakAdaSiswa.visibility = View.GONE

            binding.rvSiswaList.layoutManager = LinearLayoutManager(activity)
            binding.rvSiswaList.setHasFixedSize(true)

            val dashboardAdapter = SiswaItemsAdapter(requireActivity(),siswaItemsList)
            binding.rvSiswaList.adapter = dashboardAdapter

//            dashboardAdapter.setOnClickListener(object : sis.OnClickListener{
//                override fun onClick(position: Int, kelompok: Kelompok) {
//                    val intent = Intent(context, TaskListActivity::class.java)
//                    intent.putExtra(Constants.DOCUMENT_ID,kelompok.documentId)
//                    startActivity(intent)
//                }
//            })

        }else{
            binding.rvSiswaList.visibility = View.GONE
            binding.tvTidakAdaSiswa.visibility = View.VISIBLE
        }
    }
}

