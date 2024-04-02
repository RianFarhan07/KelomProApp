package com.example.kelomproapp.ui.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.kelomproapp.R
import com.example.kelomproapp.databinding.ActivityPanduanBinding
import kotlinx.android.synthetic.main.activity_panduan.*
import kotlinx.android.synthetic.main.activity_panduan.view.*

class PanduanActivity : AppCompatActivity() {
    private var binding : ActivityPanduanBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanduanBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        binding?.cardViewStep1?.setOnClickListener {
            showCustomDialogBox("Daftar atau masuk dengan akun Anda",
                "1. Buka aplikasi dan tekan signUp untuk mendaftar.\n\n" +
                        "2. Isi nama,email dan password. \n\n" +
                        "3. tekan signIn untuk masuk.\n\n" +
                        "4. kemudian masukkan email dan password."
            )
        }

        binding?.cardViewStep2?.setOnClickListener {
            showCustomDialogBox("Mengubah Profile",
                "1. Buka aplikasi dan masuk ke akun Anda.\n\n" +
                        "2. Tekan Drawer pada kiri atas aplikasi. \n\n" +
                        "3. Klik menu Profile Saya.\n\n" +
                        "4. ubah data profile yang ingin diganti dan tekan SUBMIT."
            )
        }

        binding?.cardViewStep3?.setOnClickListener {
            showCustomDialogBox("Membuat Kelompok",
                "1. Buka aplikasi dan masuk ke akun Anda.\n\n" +
                        "2. Di beranda atau menu utama, tekan tombol \"+\". \n\n" +
                        "3. Isi detail kelompok seperti nama kelompok dll, kemudian tekan buat kelompok.\n\n" +
                        "4. Undang anggota kelompok dengan mengirimkan undangan melalui email atau username mereka."
            )
        }

        binding?.cardViewStep4?.setOnClickListener {
            showCustomDialogBox("Membuat Tugas",
                "1. Buka aplikasi dan masuk ke akun Anda.\n\n" +
                        "2. Di beranda atau menu utama, pilih kelompok anda. \n\n" +
                        "3. Tekan Tombol tambah tugas.\n\n" +
                        "4. Masukkan nama tugas.\n\n" +
                        "5. Tekan tombol ceklis."
            )
        }

        binding?.cardViewStep5?.setOnClickListener {
            showCustomDialogBox("Menugaskan Anggota ke Kelompok",
                "1. Buka aplikasi dan masuk ke akun Anda.\n\n" +
                        "2. Di beranda atau menu utama, pilih kelompok anda.\n\n" +
                        "3. Tekan menu setting pada kanan atas aplikasi.\n\n" +
                        "4. Pilih \"Lihat Atau Tambahkan Anggota Kelompok.\"\n\n " +
                        "5. Tekan menu \"+\".\n\n" +
                        "6. Masukkan Email anggota yang ingin ditambahkan.\n\n" +
                        "7. Tekan \"TAMBAHKAN\". " +
                        "8. Tekan tombol UPDATE.\n\n"

            )
        }

        binding?.cardViewStep6?.setOnClickListener {
            showCustomDialogBox("Menugaskan Anggota ke Tugas",
                "1. Buka aplikasi dan masuk ke akun Anda.\n\n" +
                        "2. Di beranda atau menu utama, pilih kelompok anda.\n\n" +
                        "3. Pilih tugas.\n\n" +
                        "4. Tekan \"+\" pada anggota.\n\n" +
                        "5. Pilih anggota yang ingin ditugaskan.\n\n" +
                        "6. Tekan tombol UPDATE.\n\n"
            )
        }

        binding?.cardViewStep7?.setOnClickListener {
            showCustomDialogBox("Mengubah Detail Tugas",
                "1. Buka aplikasi dan masuk ke akun Anda.\n\n" +
                        "2. Di beranda atau menu utama, pilih kelompok anda.\n\n" +
                        "3. Pilih tugas.\n\n" +
                        "4. Ubah detail tugas seperti memberi tenggat waktu atau mengganti nama tugas.\n\n" +
                        "5. Tekan tombol UPDATE.\n\n"
            )
        }

        binding?.cardViewStep8?.setOnClickListener {
            showCustomDialogBox("Mengunggah File Tugas",
                "1. Buka aplikasi dan masuk ke akun Anda.\n\n" +
                        "2. Di beranda atau menu utama, pilih kelompok anda\n\n" +
                        "3. Pilih tugas.\n\n" +
                        "4. Tekan tombol Unggah PDF \n\n" +
                        "5. Pilih file PDF yang ingin di unggah \n\n" +
                        "6. Tekan tombol UPDATE.\n\n"
            )
        }

        binding?.cardViewStep9?.setOnClickListener {
            showCustomDialogBox("Melihat Daftar Tugas",
                "1. Buka aplikasi dan masuk ke akun Anda.\n\n" +
                        "2. Tekan Drawer pada kiri atas aplikasi. \n\n" +
                        "3. Klik menu Tugas Saya.\n\n"
            )
        }

        binding?.cardViewStep10?.setOnClickListener {
            showCustomDialogBox("Melihat Daftar Tugas",
                "1. Buka aplikasi dan masuk ke akun Anda.\n\n" +
                        "2. Tekan Drawer pada kiri atas aplikasi. \n\n" +
                        "3. Klik menu Materi Saya.\n\n" +
                        "4. Pilih materi yang ingin di unduh.\n\n"
            )
        }

        binding?.cardViewStep11?.setOnClickListener {
            showCustomDialogBox("Menghapus Kelompok",
                "1. Buka aplikasi dan masuk ke akun Anda.\n\n" +
                        "2. Di beranda atau menu utama, pilih kelompok anda.\n\n" +
                        "3. Tekan menu setting pada kanan atas aplikasi.\n\n" +
                        "4. Tekan tombol \"Hapus Kelompok.\"\n\n "
            )
        }

        binding?.cardViewStep12?.setOnClickListener {
            showCustomDialogBox("Menghapus Tugas",
                "1. Buka aplikasi dan masuk ke akun Anda.\n\n" +
                        "2. Di beranda atau menu utama, pilih kelompok anda.\n\n" +
                        "3. Pilih tugas.\n\n" +
                        "4. Pilih menu hapus pada kanan atas aplikasi .\n\n"
            )
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarPanduanActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding?.toolbarPanduanActivity?.setNavigationOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showCustomDialogBox(title:String?, message: String?){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_panduan)

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_message)
        val tvTutup = dialog.findViewById<Button>(R.id.btn_tutup)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title_panduan)

        tvTitle.text = title
        tvMessage.text = message

        tvTutup.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}