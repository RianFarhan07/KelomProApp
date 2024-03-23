package com.example.kelomproapp.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.kelomproapp.models.Guru
import com.example.kelomproapp.models.Kelompok
import com.example.kelomproapp.models.Siswa
import com.example.kelomproapp.ui.activities.*
import com.example.kelomproapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlin.math.log

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerSiswa(activity: SignUpActivity, siswaInfo: Siswa){
        mFireStore.collection(Constants.SISWA)
            .document(siswaInfo.id)
            .set(siswaInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.siswaRegistrationSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error Ketika Registrasi User"
                )
            }
    }

    fun registerGuru(activity: SignUpGuruActivity, guruInfo: Guru){
        mFireStore.collection(Constants.GURU)
            .document(guruInfo.id)
            .set(guruInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.guruRegistrationSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error Ketika Registrasi User"
                )
            }
    }

    fun getCurrentUserID() : String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserRole(userId: String, onComplete: (String?) -> Unit) {
        mFireStore.collection(Constants.SISWA)
            .document(userId)
            .get()
            .addOnSuccessListener { siswaDocument ->
                if (siswaDocument.exists()) {
                    onComplete(Constants.SISWA) // Mengembalikan peran siswa
                } else {
                    // Jika tidak ditemukan sebagai siswa, cek sebagai guru
                    mFireStore.collection(Constants.GURU)
                        .document(userId)
                        .get()
                        .addOnSuccessListener { guruDocument ->
                            if (guruDocument.exists()) {
                                onComplete(Constants.GURU) // Mengembalikan peran guru
                            } else {
                                onComplete(null) // Jika tidak ditemukan sebagai guru, kembalikan null
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreClass", "Error getting user role (guru): ${e.message}", e)
                            onComplete(null)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error getting user role (siswa): ${e.message}", e)
                onComplete(null)
            }
    }



    fun getUserDetails(activity: Activity, role: String, readKelompokList: Boolean = false) {
        val userCollection = if (role == "siswa") Constants.SISWA else Constants.GURU

        mFireStore.collection(userCollection)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    if (role == "siswa") {
                        val loggedInSiswa = document.toObject(Siswa::class.java)
                        Log.i(activity.javaClass.simpleName, document.toString())

                        loggedInSiswa?.let { siswa ->
                            when (activity) {
                                is SignInActivity -> {
                                    activity.userLoggedInSuccess(siswa)
                                }
                                is MainActivity -> {
                                    activity.updateNavigationUserDetails(siswa, readKelompokList)
                                }
                                is MyProfileActivity -> {
                                    activity.setUserDataInUI(siswa)
                                }
                                is KelompokDetailsActivity -> {
                                    activity.setUserDataInUI(siswa)
                                }
                            }
                        }
                    } else if (role == "guru") {
                        val loggedInGuru = document.toObject(Guru::class.java)
                        Log.i(activity.javaClass.simpleName, document.toString())

                        loggedInGuru?.let { guru ->
                            when (activity) {
                                is SignInActivity -> {
                                    activity.userLoggedInSuccess(guru)
                                }
                                is GuruMainActivity -> {
                                    activity.updateNavigationGuruDetails(guru, readKelompokList)
                                }
                            }
                        }
                    }
                } else {
                    Log.e(activity.javaClass.simpleName.toString(), "Dokumen tidak ditemukan")
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is GuruMainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName.toString(),
                    "Error Mengambil data detail user", e)
            }
    }



//    fun getGuruDetails(activity : Activity, readKelompokList: Boolean = false) {
//        mFireStore.collection(Constants.GURU)
//            .document(getCurrentUserID())
//            .get()
//            .addOnSuccessListener { document ->
//                val loggedInGuru = document.toObject(Guru::class.java)
//                Log.i(activity.javaClass.simpleName, document.toString())
//
//                when(activity){
//                    is SignInActivity -> {
//                        if (loggedInGuru != null) {
//                            activity.userLoggedInSuccess(loggedInGuru)
//                        }
//                    }
//                    is GuruMainActivity -> {
//                        if (loggedInGuru != null) {
//                            activity.updateNavigationGuruDetails(loggedInGuru,readKelompokList)
//                        }
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                when(activity){
//                    is SignInActivity -> {
//                        activity.hideProgressDialog()
//                    }
//
//                }
//
//                Log.e(activity.javaClass.simpleName.toString(),
//                    "Error Mengambil data detail user")
//            }
//    }


    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.SISWA)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile Data Update Successfully")
                Toast.makeText(activity,"profile update successfully", Toast.LENGTH_LONG).show()
                when(activity) {
                    is MyProfileActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {
                    e->
                when(activity) {
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while update a profile",
                    e
                )
                Toast.makeText(activity,"ERROR while update a profile", Toast.LENGTH_LONG).show()
            }
    }

    fun createKelompok(activity : CreateKelompokActivity, kelompok: Kelompok){
        mFireStore.collection(Constants.KELOMPOK)
            .document()
            .set(kelompok, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Kelompok berhasil dibuat")
                Toast.makeText(activity,"Berhasil membuat kelompok", Toast.LENGTH_LONG).show()
                activity.kelompokCreatedSuccessfully()

            }.addOnFailureListener {
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error membuat kelompok",e)
            }
    }

    fun getKelompokList(activity: MainActivity){
        mFireStore.collection(Constants.KELOMPOK)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val kelompokList : ArrayList<Kelompok> = ArrayList()
                for(i in document.documents){
                    val kelompok = i.toObject(Kelompok::class.java)!!
                    kelompok.documentId = i.id
                    kelompokList.add(kelompok)
                }

                activity.populateKelompokListToUI(kelompokList)
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error mendapatkan kelompok")
            }
    }

    fun getKelompokListGuru(activity: GuruMainActivity){
        mFireStore.collection(Constants.KELOMPOK)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val kelompokList : ArrayList<Kelompok> = ArrayList()
                for(i in document.documents){
                    val kelompok = i.toObject(Kelompok::class.java)!!
                    kelompok.documentId = i.id
                    kelompokList.add(kelompok)
                }

                activity.populateKelompokListToUI(kelompokList)
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error mendapatkan kelompok")
            }
    }

    fun getKelompokDetails(activity: Activity, documentId: String) {
        mFireStore.collection(Constants.KELOMPOK)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                val kelompok = document.toObject(Kelompok::class.java)
                kelompok?.documentId = document.id

                if (activity is KelompokDetailsActivity){
                    activity.setKelompokDataInUI(kelompok!!)
                }
                if (activity is TaskListActivity){
                    activity.kelompokDetails(kelompok!!)
                }

            }
            .addOnFailureListener { e ->

                if (activity is KelompokDetailsActivity) {
                    activity.hideProgressDialog()
                }
                if (activity is TaskListActivity) {
                    activity.hideProgressDialog()
                }

                Log.e(activity.javaClass.simpleName, "Error fetching kelompok details: ${e.message}")
            }
    }

    fun deleteKelompok(activity: KelompokDetailsActivity,kelompokId: String){
        mFireStore.collection(Constants.KELOMPOK)
            .document(kelompokId)
            .delete()
            .addOnSuccessListener {
                activity.kelompokDeleteSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName,
                    "Error while deleting kelompok",e)
            }
    }

    fun updateKelompokData(activity: Activity, kelompokHashMap: HashMap<String, Any>, kelompokId : String){
        mFireStore.collection(Constants.KELOMPOK)
            .document(kelompokId)
            .update(kelompokHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Kelompok Data Update Successfully")
                Toast.makeText(activity,"kelompok update successfully", Toast.LENGTH_LONG).show()
                when(activity) {
                    is KelompokDetailsActivity -> {
                        activity.kelompokUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {
                    e->
                when(activity) {
                    is KelompokDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while update a profile",
                    e
                )
                Toast.makeText(activity,"ERROR while update a profile", Toast.LENGTH_LONG).show()
            }
    }

    fun getAssignedAnggotaListDetails(activity: Activity, assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.SISWA)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(activity.javaClass.simpleName,document.documents.toString())

                val siswaList : ArrayList<Siswa> = ArrayList()

                for (i in document.documents){
                    val siswa = i.toObject(Siswa::class.java)!!
                    siswaList.add(siswa)
                }

                when(activity) {
                    is AnggotaActivity ->  {
                        activity.setupAnggotaList(siswaList)
                    }
                    is TaskListActivity -> {
                        activity.anggotaKelompokDetailList(siswaList)
                    }
                }

            }
            .addOnFailureListener {
                    e ->
                when(activity) {
                    is AnggotaActivity -> {
                        activity.hideProgressDialog()
                    }
                    is TaskListActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,"error creating members",e)

            }
    }

    fun getAnggotaDetails(activity: AnggotaActivity, email: String){
        mFireStore.collection(Constants.SISWA)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {
                    document ->
                if (document.documents.size > 0){
                    val siswa = document.documents[0].toObject(Siswa::class.java)!!
                    activity.anggotaDetails(siswa)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("TIDAK ADA USER DITEMUKAN",true)
                }
            }
            .addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details",
                    e
                )
            }
    }

    fun assignedAnggotaToKelompok(activity: AnggotaActivity, kelompok: Kelompok, siswa: Siswa){

        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = kelompok.assignedTo

        mFireStore.collection(Constants.KELOMPOK)
            .document(kelompok.documentId.toString())
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.anggotaAssignedSuccess(siswa)
            }
            .addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error memasukkan anggota",e)
            }
    }


//    fun getTaskDetails(activity: TaskDetailsActivity, documentId: String) {
//        mFireStore.collection(Constants.TASK)
//            .document(documentId)
//            .get()
//            .addOnSuccessListener { document ->
//                val task = document.toObject(Task::class.java)
//                task?.documentId = document.id
//
//                activity.setTaskDataInUI(task!!)
//
//            }
//            .addOnFailureListener { e ->
//
//                activity.hideProgressDialog()
//                Log.e(activity.javaClass.simpleName, "Error fetching task details: ${e.message}")
//            }
//    }

    fun addUpdateTaskList(activity: Activity, kelompok: Kelompok){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = kelompok.taskList

        mFireStore.collection(Constants.KELOMPOK)
            .document(kelompok.documentId.toString())
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully")
                when (activity) {
                    is TaskListActivity -> {
                        activity.addUpdateTaskListSuccess()
                    }
                    is TaskDetailsActivity -> {
                        activity.addUpdateTaskListSuccess()
                    }

                }
            }
            .addOnFailureListener {
                    exception ->
                when(activity) {
                    is TaskListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is TaskDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,"Error while updating TaskList")
            }
    }

}