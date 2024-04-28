package com.example.kelomproapp.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.kelomproapp.models.*
import com.example.kelomproapp.ui.activities.*
import com.example.kelomproapp.ui.fragments.GuruFragment
import com.example.kelomproapp.ui.fragments.KelompokFragment
import com.example.kelomproapp.ui.fragments.MateriFragment
import com.example.kelomproapp.ui.fragments.SiswaFragment
import com.example.kelomproapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    interface KelompokSearchListener {
        fun onSearchComplete(kelompokList: ArrayList<Kelompok>)
    }
    interface MateriSearchListener {
        fun onSearchComplete(materi: ArrayList<Materi>)
    }


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

    fun deleteSiswa(fragment: SiswaFragment, siswaId: String) {
        mFireStore.collection(Constants.SISWA)
            .document(siswaId)
            .delete()
            .addOnSuccessListener {
                fragment.deleteSiswaSuccess()


            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while deleting siswa", e)
            }
    }

    fun deleteSiswa(Activity: MainActivity, siswaId: String) {
        mFireStore.collection(Constants.SISWA)
            .document(siswaId)
            .delete()
            .addOnSuccessListener {
                Activity.deleteSiswaSuccess()

            }
            .addOnFailureListener { e ->
                Activity.hideProgressDialog()
                Log.e(Activity.javaClass.simpleName, "Error while deleting siswa", e)
            }
    }

    fun deleteMateri(fragment: MateriFragment, materiId: String) {
        mFireStore.collection(Constants.MATERI)
            .document(materiId)
            .delete()
            .addOnSuccessListener {
                fragment.materiDeleteSuccess()


            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while deleting materi", e)
            }
    }

    fun deleteGuru(fragment: GuruFragment, guruId: String) {
        mFireStore.collection(Constants.GURU)
            .document(guruId)
            .delete()
            .addOnSuccessListener {
                fragment.deleteGuruSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while deleting guru", e)
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
                                is CourseActivity -> {
                                    activity.getGuruName(guru)
                                }
                                is CourseKelompokActivity -> {
                                    activity.getGuruName(guru)
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

    fun searchKelompokList(query: String?, listener: KelompokSearchListener) {
        val lowerCaseQuery = query?.toLowerCase() ?: "" // Mengonversi query ke huruf kecil

        mFireStore.collection(Constants.KELOMPOK)
            .get()
            .addOnSuccessListener { documents ->
                val searchResults = ArrayList<Kelompok>()
                for (document in documents) {
                    val kelompok = document.toObject(Kelompok::class.java)
                    val topic = kelompok.topic?.toLowerCase() // Mengonversi topik kelompok ke huruf kecil
                    if (topic != null && topic.contains(lowerCaseQuery)) {
                        searchResults.add(kelompok)
                    }
                }
                listener.onSearchComplete(searchResults)
            }
            .addOnFailureListener { exception ->
                listener.onSearchComplete(ArrayList()) // Jika ada kesalahan, kembalikan daftar kosong
            }
    }



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
                    is MainActivity -> {
                        activity.tokenUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {
                    e->
                when(activity) {
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
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

    fun getAllKelompokList(fragment: KelompokFragment){
        mFireStore.collection(Constants.KELOMPOK)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val kelompokList : ArrayList<Kelompok> = ArrayList()
                for(i in document.documents){
                    val kelompok = i.toObject(Kelompok::class.java)!!
                    kelompok.documentId = i.id
                    kelompokList.add(kelompok)
                }

                fragment.successKelompokItemsList(kelompokList)
            }.addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error mendapatkan kelompok")
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
                if (activity is CourseKelompokDetailActivity){
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

    fun createCourse(activity : CourseActivity, course: Course){
        mFireStore.collection(Constants.COURSE)
            .document()
            .set(course, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "course berhasil dibuat")
                Toast.makeText(activity,"Berhasil membuat course", Toast.LENGTH_LONG).show()
                activity.courseCreatedSuccessfully()

            }.addOnFailureListener {
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error membuat course",e)
            }
    }

    fun updateCourse(activity: CreateKelompokActivity, course: Course) {
        mFireStore.collection(Constants.COURSE)
            .document(course.documentId)
            .set(course, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("FirestoreClass", "Corse updated successfully")
                Toast.makeText(activity,"Berhasil mengupdate course", Toast.LENGTH_LONG).show()
                activity.kelompokCreatedSuccessfully()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreClass", "Error updating course: ${exception.message}")
            }
    }

    fun getCourseList(activity: CourseActivity){
        mFireStore.collection(Constants.COURSE)
//            .whereArrayContains(Constants.GURU_MAPEL, guruName)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val courseList : ArrayList<Course> = ArrayList()
                for(i in document.documents){
                    val course = i.toObject(Course::class.java)!!
                    course.documentId = i.id
                    courseList.add(course)
                }

                activity.populateCourseListToUI(courseList)
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error mendapatkan kelompok")
            }
    }

    fun getCourseDetails(activity: Activity, documentId: String) {
        mFireStore.collection(Constants.COURSE)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                val course = document.toObject(Course::class.java)
                course?.documentId = document.id

                when (activity) {
                    is CourseTopicActivity -> {
                        activity.CourseDetails(course!!)
                    }
                    is CourseKelompokActivity -> {
                        activity.CourseDetails(course!!)
                    }
                    is CreateKelompokActivity -> {
                        activity.CourseDetails(course!!)
                    }
                    is CourseTaskActivity -> {
                        activity.CourseDetails(course!!)
                }

                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is CourseTopicActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CourseKelompokActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CreateKelompokActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CourseTaskActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error fetching kelompok details: ${e.message}")
            }
    }

//    fun getCourseListForKelompokDelete(activity: KelompokDetailsActivity, kelompokId: String) {
//        mFireStore.collection(Constants.COURSE)
//            .whereArrayContains("kelompok", kelompokId)
//            .get()
//            .addOnSuccessListener { documents ->
//                if (!documents.isEmpty) {
//                    val kelasId = documents.documents[0].id
//                    deleteKelompokFromClasses(activity, kelasId, kelompokId)
//                } else {
//                    activity.hideProgressDialog()
//                    Toast.makeText(activity, "Kelas tidak ditemukan", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .addOnFailureListener { e ->
//                activity.hideProgressDialog()
//                Log.e(activity.javaClass.simpleName, "Error fetching classes list: ${e.message}")
//                Toast.makeText(activity, "Gagal mengambil data kelas", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    fun deleteKelompokFromCourse(
//        activity: KelompokDetailsActivity,
//        courseId: String,
//        kelompokId: String
//    ) {
//        mFireStore.collection(Constants.COURSE)
//            .document(courseId)
//            .get()
//            .addOnSuccessListener { documentSnapshot ->
//                val course = documentSnapshot.toObject(Course::class.java)
//                if (course != null) {
//                    val updatedKelompokList = course.kelompok.filter { it.documentId != kelompokId }
//                    course.kelompok = ArrayList(updatedKelompokList)
//
//                    mFireStore.collection(Constants.COURSE)
//                        .document(courseId)
//                        .set(course)
//                        .addOnSuccessListener {
//                            Log.e(activity.javaClass.simpleName, "Kelompok berhasil dihapus dari kelas")
//                            Toast.makeText(
//                                activity,
//                                "Berhasil menghapus kelompok dari kelas",
//                                Toast.LENGTH_LONG
//                            ).show()
//                            // Update UI atau lakukan tindakan lain setelah penghapusan kelompok dari kelas
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e(activity.javaClass.simpleName, "Error menghapus kelompok dari kelas", e)
//                            Toast.makeText(
//                                activity,
//                                "Gagal menghapus kelompok dari kelas",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e(activity.javaClass.simpleName, "Error mendapatkan data kelas", e)
//                Toast.makeText(activity, "Gagal mendapatkan data kelas", Toast.LENGTH_LONG).show()
//            }
//    }

    fun getTopicList(activity: CourseTopicActivity){
        mFireStore.collection(Constants.TOPIC)
//            .whereArrayContains(Constants.GURU_MAPEL, guruName)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val topicList : ArrayList<Topic> = ArrayList()
                for(i in document.documents){
                    val topic = i.toObject(Topic::class.java)!!
                    topic.documentId = i.id
                    topicList.add(topic)
                }

                activity.populateTopicListToUI(topicList)
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error mendapatkan kelompok")
            }
    }

    fun getTopicDetails(activity: Activity, documentId: String) {
        mFireStore.collection(Constants.TOPIC)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                val topic = document.toObject(Topic::class.java)
                topic?.documentId = document.id

                when (activity) {
                    is CourseKelompokActivity -> {
                        activity.TopicDetails(topic!!)
                    }

                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is CourseTopicActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error fetching kelompok details: ${e.message}")
            }
    }

    fun addUpdateTopicList(activity: Activity, course: Course){
        val topicListHashMap = HashMap<String, Any>()
        topicListHashMap[Constants.TOPIC_LIST] = course.topicList

        mFireStore.collection(Constants.COURSE)
            .document(course.documentId.toString())
            .update(topicListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully")
                when (activity) {
                    is CourseTopicActivity -> {
                        activity.topicCreatedSuccessfully()
                    }
                    is CreateKelompokActivity -> {
                        activity.kelompokCreatedSuccessfully()
                    }
                    is CourseKelompokDetailActivity -> {
                        activity.addUpdateTopicListSuccess()
                    }
                    is CourseTaskActivity -> {
                        activity.courseCreatedSuccessfully()
                    }
                    is CourseTaskDetailActivity -> {
                        activity.addUpdateTopicListSuccess()
                    }

                }
            }
            .addOnFailureListener {
                    exception ->
                when(activity) {
                    is CourseTopicActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CreateKelompokActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CourseKelompokDetailActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CourseTaskActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CourseTaskDetailActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,"Error while updating TaskList")
            }
    }

    fun addUpdateKelompokList(activity: Activity, course: Course){
        val topicListHashMap = HashMap<String, Any>()
        topicListHashMap[Constants.TOPIC_LIST] = course.topicList

        mFireStore.collection(Constants.COURSE)
            .document(course.documentId.toString())
            .update(topicListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully")
                when (activity) {
                    is CourseTopicActivity -> {
                        activity.topicCreatedSuccessfully()
                    }

                }
            }
            .addOnFailureListener {
                    exception ->
                when(activity) {
                    is CourseTopicActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,"Error while updating TaskList")
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
                    is CourseTaskActivity -> {
                        activity.anggotaKelompokDetailList(siswaList)
                    }
//                    is CourseKelompokActivity -> {
//                        activity.anggotaKelompokDetailList(siswaList)
//                    }
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
                    is CourseTaskActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CourseKelompokActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,"error creating members",e)

            }
    }

    fun getSiswaListDetails(fragment: SiswaFragment){
        mFireStore.collection(Constants.SISWA)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(fragment.javaClass.simpleName,document.documents.toString())

                val siswaList : ArrayList<Siswa> = ArrayList()

                for (i in document.documents){
                    val siswa = i.toObject(Siswa::class.java)!!
                    siswaList.add(siswa)
                }

                fragment.successSiswaItemsList(siswaList)
            }
            .addOnFailureListener {
                    e ->

                fragment.hideProgressDialog()

                Log.e(fragment.javaClass.simpleName,"error creating members",e)

            }
    }

    fun getGuruListDetails(fragment: GuruFragment){
        mFireStore.collection(Constants.GURU)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(fragment.javaClass.simpleName,document.documents.toString())

                val guruList : ArrayList<Guru> = ArrayList()

                for (i in document.documents){
                    val guru = i.toObject(Guru::class.java)!!
                    guruList.add(guru)
                }

                fragment.successGuruItemsList(guruList)
            }
            .addOnFailureListener {
                    e ->

                fragment.hideProgressDialog()

                Log.e(fragment.javaClass.simpleName,"error creating members",e)

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

    fun unassignAnggotaFromKelompok(activity: Activity, kelompok: Kelompok, siswa: Siswa) {
        val assignedTo = kelompok.assignedTo
        assignedTo.remove(siswa.id) // Menghapus ID anggota dari ArrayList assignedTo

        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = assignedTo

        mFireStore.collection(Constants.KELOMPOK)
            .document(kelompok.documentId.toString())
            .update(assignedToHashMap)
            .addOnSuccessListener {

                when(activity) {
                    is AnggotaActivity ->  {
                        activity.anggotaUnassignedSuccess(siswa)
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity) {
                    is AnggotaActivity ->  {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error mengunassign anggota", e)
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

    fun getAssignedTaskList(activity: MyTaskActivity) {
        mFireStore.collection(Constants.KELOMPOK)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener { documents ->
                val taskList: ArrayList<Task> = ArrayList()
                for (document in documents) {
                    val kelompok = document.toObject(Kelompok::class.java)
                    taskList.addAll(kelompok.taskList.filter { task -> getCurrentUserID() in task.assignedTo })
                }
                activity.populateTaskListToUI(taskList)
            }
            .addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error fetching assigned task list: ${exception.message}")
            }
    }

    fun createMateri(activity: CreateMateriActivity, materi: Materi) {
        mFireStore.collection(Constants.MATERI)
            .document(materi.id)
            .set(materi, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Materi berhasil dibuat")
                Toast.makeText(activity, "Berhasil membuat materi", Toast.LENGTH_LONG).show()
                activity.materiCreatedSuccessfully()

            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error membuat materi", e)
            }
    }

    fun getAllMateriListFragment(fragment: MateriFragment){
        mFireStore.collection(Constants.MATERI)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val materiList : ArrayList<Materi> = ArrayList()
                for(i in document.documents){
                    val materi = i.toObject(Materi::class.java)!!
                    materi.id = i.id
                    materiList.add(materi)
                }

                fragment.successMateriItemsList(materiList)
            }.addOnFailureListener {
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error mendapatkan kelompok")
            }
    }

    fun getAllMateriList(Activity: MateriActivity){
        mFireStore.collection(Constants.MATERI)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.e(Activity.javaClass.simpleName, document.documents.toString())
                val materiList : ArrayList<Materi> = ArrayList()
                for(i in document.documents){
                    val materi = i.toObject(Materi::class.java)!!
                    materi.id = i.id
                    materiList.add(materi)
                }

                Activity.populateMateriListToUI(materiList)
            }.addOnFailureListener {
                Activity.hideProgressDialog()
                Log.e(Activity.javaClass.simpleName, "Error mendapatkan kelompok")
            }
    }

    fun getMateriDetails(activity: CreateMateriActivity, documentId: String) {
        mFireStore.collection(Constants.MATERI)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                val materi = document.toObject(Materi::class.java)
                materi?.id = document.id
                if (materi != null) {
                    activity.showMateriDetails(materi)
                }

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error fetching materi details: ${e.message}")
            }
    }

    fun updateMateri(activity: CreateMateriActivity, materiId: String, materi: Materi) {
        mFireStore.collection(Constants.MATERI)
            .document(materiId)
            .set(materi, SetOptions.merge())
            .addOnSuccessListener {
                activity.materiUpdatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while updating materi", e)
                Toast.makeText(activity, "Failed to update materi: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }



    fun searchMateriList(query: String?, listener: MateriSearchListener) {
        val lowerCaseQuery = query?.toLowerCase() // Mengonversi query ke huruf kecil

        mFireStore.collection(Constants.MATERI)
            .get()
            .addOnSuccessListener { documents ->
                val searchResults = ArrayList<Materi>()
                for (document in documents) {
                    val materi = document.toObject(Materi::class.java)
                    val topic = materi.topic?.toLowerCase() // Mengonversi topik kelompok ke huruf kecil
                    if (topic != null && lowerCaseQuery != null && topic.contains(lowerCaseQuery)) {
                        searchResults.add(materi)
                    }
                }
                listener.onSearchComplete(searchResults)
            }
            .addOnFailureListener { exception ->
                listener.onSearchComplete(ArrayList()) // Jika ada kesalahan, kembalikan daftar kosong
            }
    }



}