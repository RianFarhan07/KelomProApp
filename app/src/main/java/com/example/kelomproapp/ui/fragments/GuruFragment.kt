package com.example.kelomproapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.GuruItemsAdapter
import com.example.kelomproapp.adapter.SiswaItemsAdapter
import com.example.kelomproapp.databinding.FragmentGuruBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Guru
import com.example.shopeekwapp.ui.fragments.BaseFragment

class GuruFragment : BaseFragment() {

    private var _binding: FragmentGuruBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuruBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getGuruItemList()
    }

    fun getGuruItemList(){
        showProgressDialog(resources.getString(R.string.mohon_tunggu))

        FirestoreClass().getGuruListDetails(this)
    }

    fun successGuruItemsList(guruItemList: ArrayList<Guru>){
        hideProgressDialog()

        if (guruItemList.size > 0){
            binding.rvGuruList.visibility = View.VISIBLE
            binding.tvTidakAdaGuru.visibility = View.GONE

            binding.rvGuruList.layoutManager = LinearLayoutManager(activity)
            binding.rvGuruList.setHasFixedSize(true)

            val dashboardAdapter = GuruItemsAdapter(requireActivity(),guruItemList)
            binding.rvGuruList.adapter = dashboardAdapter

//            dashboardAdapter.setOnClickListener(object : sis.OnClickListener{
//                override fun onClick(position: Int, kelompok: Kelompok) {
//                    val intent = Intent(context, TaskListActivity::class.java)
//                    intent.putExtra(Constants.DOCUMENT_ID,kelompok.documentId)
//                    startActivity(intent)
//                }
//            })

        }else{
            binding.rvGuruList.visibility = View.GONE
            binding.tvTidakAdaGuru.visibility = View.VISIBLE
        }
    }
}
