package com.example.kelomproapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelomproapp.R
import com.example.kelomproapp.adapter.SiswaItemsAdapter
import com.example.kelomproapp.databinding.FragmentSiswaBinding
import com.example.kelomproapp.firebase.FirestoreClass
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.utils.SwipeToDeleteCallback
import com.example.shopeekwapp.ui.fragments.BaseFragment
import com.google.firebase.auth.FirebaseAuth

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

            val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    showProgressDialog(resources.getString(R.string.mohon_tunggu))
                    FirestoreClass().deleteSiswa(this@SiswaFragment,
                        siswaItemsList[viewHolder.adapterPosition].id)

                }
            }
            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(binding.rvSiswaList)

        }else{
            binding.rvSiswaList.visibility = View.GONE
            binding.tvTidakAdaSiswa.visibility = View.VISIBLE
        }
    }

    fun deleteSiswaSuccess(){
        hideProgressDialog()
        Toast.makeText(context,"Berhasil menghapus akun siswa",
            Toast.LENGTH_LONG).show()

        getSiswaItemList()
    }
}

