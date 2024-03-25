package com.example.kelomproapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import com.example.kelomproapp.databinding.FragmentKelompokBinding

class KelompokFragment : Fragment() {

    private var _binding: FragmentKelompokBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKelompokBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textKelompok
        textView.text = "Kelompok Fragment"

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
                // Action when the search button is pressed
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Action while typing into the search bar
                return false
            }
        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
