package com.example.kelomproapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
