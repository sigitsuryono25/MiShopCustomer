@file:Suppress("DEPRECATION")

package com.lauwba.surelabs.mishopcustomer.registrasi

import agency.tango.materialintroscreen.MessageButtonBehaviour
import agency.tango.materialintroscreen.listeners.IFinishListener
import android.app.ProgressDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.lauwba.surelabs.mishopcustomer.MainActivity
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.MaterialIntroActivity
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import com.lauwba.surelabs.mishopcustomer.registrasi.ui.newregistrasi.AddressingFragment
import com.lauwba.surelabs.mishopcustomer.registrasi.ui.newregistrasi.BasicInformasi
import org.jetbrains.anko.*
import java.io.File

class NewRegistrasi : MaterialIntroActivity(), IFinishListener,
    BasicInformasi.OnDataPass,
    AddressingFragment.OnAddressingPass {

    override fun doOnFinish() {

    }


    private var cstmr: Customer? = null
    private var password: String? = null
    private var realPath: String? = null
    private var mStorageRef: StorageReference? = null
    private var pd: ProgressDialog? = null

    override fun onDataPass(c: Customer?, url: String?, pswd: String) {
        cstmr?.nama = c?.nama
        cstmr?.noKTP = c?.noKTP
        cstmr?.email = c?.email
        cstmr?.telepon = c?.telepon
        cstmr?.nama = c?.nama
        cstmr?.gender = c?.gender
        password = pswd
        realPath = url
    }

    override fun onAddressingPass(c: Customer) {
        cstmr?.alamat = c.alamat
        cstmr?.statusAktif = c.statusAktif
        cstmr?.masaSuspend = c.masaSuspend
        cstmr?.terdaftar = c.terdaftar
        cstmr?.kabupaten = c.kabupaten

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        enableLastSlideAlphaExitTransition(true)

        backButtonTranslationWrapper.setEnterTranslation { view: View, fl: Float ->
            view.alpha = fl
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//            return
        }
        cstmr = Customer()
        addSlide(BasicInformasi())
        addSlide(
            AddressingFragment(),
            MessageButtonBehaviour(
                View.OnClickListener {
                    showMessage("Try us!")
                },
                "Tools"
            )

        )


    }


    private fun updateFileIntoFirebase(realPath: String?) {
        pd = ProgressDialog.show(this@NewRegistrasi, "", "Mengunggah Foto..", false, false)
        mStorageRef = Constant.storage.reference
        var urlDownload: String
        val file = Uri.fromFile(File(realPath))
        val riversRef = mStorageRef?.child("images/${cstmr?.terdaftar}/${cstmr?.terdaftar}.jpg")

        riversRef?.putFile(file)
            ?.addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                Log.i("PROGRESS", "$progress % done")
            }
            ?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation riversRef.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    try {
                        pd?.dismiss()
                        urlDownload = task.result.toString()
                        insertDatabase(urlDownload)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    pd?.dismiss()
                }
            }
    }

    private fun insertDatabase(urlDownload: String) {
        pd = ProgressDialog.show(this@NewRegistrasi, "", "Mengirimkan Informasi Anda..", false, false)
        cstmr?.fotoCustomer = urlDownload
        cstmr?.uid = Constant.mAuth.currentUser?.uid
        cstmr?.token = FirebaseInstanceId.getInstance().token
        val ref = Constant.database.getReference(Constant.TB_CUSTOMER)
        val key = ref.push().key ?: ""
        ref.child(key).setValue(cstmr)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    pd?.dismiss()
                    alert {
                        message = "Pendaftaran Berhasil. Silahkan Login"
                        okButton {
                            finish()
                            startActivity(intentFor<MainActivity>().clearTop().clearTask().newTask())
                        }
                    }.show()
                }

            }
    }

    override fun sendData() {
        super.sendData()
        Log.d("email", cstmr?.email)
        Log.d("password", password)
        alert {
            message = "Apakah Anda Sudah Yakin Dengan Data Yang Diisikan?" +
                    "\nSemua data yang di isikan harus dapat dipertanggung jawabkan dikemudian hari"
            okButton {
                pd = ProgressDialog.show(this@NewRegistrasi, "", "Membuat user..", false, false)
                cstmr?.email?.let { it1 ->
                    password?.let { it2 ->
                        Constant.mAuth.createUserWithEmailAndPassword(it1, it2)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    pd?.dismiss()
                                    updateFileIntoFirebase(realPath)
                                }
                            }
                            .addOnFailureListener {
                                pd?.dismiss()
                                toast(it.message.toString())
                            }
                    }
                }
            }
        }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {

        }
    }
}
