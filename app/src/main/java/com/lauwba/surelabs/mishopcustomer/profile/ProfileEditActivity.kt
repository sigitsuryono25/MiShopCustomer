@file:Suppress("DEPRECATION")

package com.lauwba.surelabs.mishopcustomer.profile

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.config.HourToMillis
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import kotlinx.android.synthetic.main.activity_profile_edit.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.io.File

class ProfileEditActivity : AppCompatActivity() {
    private var email: String? = null
    private var pd: ProgressDialog? = null
    private var realPath: String? = null
    private var mStorageRef: StorageReference? = null
    private var lastUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        titleToolbar.text = "Edit Profile"

        try {
            val data = intent.getSerializableExtra("data") as Customer
            nama.setText(data.nama)
            alamat.setText(data.alamat)
            nomorTelepon.setText(data.telepon)
            Glide.with(this)
                .load(data.fotoCustomer)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(fotouser)
            lastUrl = data.fotoCustomer
            email = data.email
        } catch (e: Exception) {

        }

        initView()
    }

    private fun initView() {
        simpan.onClick {
            if (nama.text.isNullOrEmpty()) {
                nama.error = "Nama Harus Diisi"
            } else if (alamat.text.isNullOrEmpty()) {
                alamat.error = "Alamat Harus Diisi"
            } else if (nomorTelepon.text.isNullOrEmpty()) {
                nomorTelepon.error = "Nomor Telepon Harus Diisi"
            } else {
                passwordConfirm()
            }
        }

        pilih.onClick {
            showPicker(Constant.GALERYREQ, "image/*")
        }


    }

    private fun showPicker(i: Int, s: String) {
        val picker = Intent(Intent.ACTION_PICK)
        picker.type = s
        startActivityForResult(picker, i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.GALERYREQ && resultCode == RESULT_OK) {
            realPath = getRealPathFromUri(this, Uri.parse(data?.dataString))
        }
        try {
            showPreview(realPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showPreview(realPath: String?) {
        Glide.with(this)
            .load(realPath)
            .apply(RequestOptions().centerCrop().circleCrop())
            .into(fotouser)
    }

    private fun getRealPathFromUri(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val project = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, project, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }

    private fun passwordConfirm() {
        var password: EditText? = null
        alert {
            customView {
                verticalLayout {
                    padding = dip(8)
                    password = editText {
                        hint = "Password Anda"
                        inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                }
            }

            okButton {
                authentiationUser(password?.text?.toString())
            }

            cancelButton {

            }
        }.show()
    }

    private fun authentiationUser(password: String?) {
        pd = ProgressDialog.show(this, "", "Mengautentikasi...", false, false)
        val id = HourToMillis.millis().toString()
        email?.let {
            password?.let { it1 ->
                Constant.mAuth.signInWithEmailAndPassword(it, it1)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            pd?.dismiss()
                            if (realPath.isNullOrEmpty()) {
                                lastUrl?.let { it2 -> updateDataUser(it2, HourToMillis.millis().toString()) }
                            } else {
                                uploadImageUser(id)
                            }
                        }
                    }
            }
        }
    }

    private fun uploadImageUser(id: String) {
        pd = ProgressDialog.show(this, "", "Mengunggah Foto...", false, false)
        mStorageRef = Constant.storage.reference
        var urlDownload = ""
        val file = Uri.fromFile(File(realPath))
        val riversRef = mStorageRef?.child("images/user/$id.jpg")

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
                    pd?.dismiss()
                    urlDownload = task.result.toString()
                    updateDataUser(urlDownload, id)
                } else {
                }
            }
    }

    private fun updateDataUser(urlDownload: String, id: String) {
        pd = ProgressDialog.show(this, "", "Mengupdate Data Customer...", false, false)
        var key = ""
        val ref = Constant.database.getReference(Constant.TB_CUSTOMER)
        ref.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (issue in p0.children) {
                        key = issue.key.toString()
                        ref.child(key).child("nama").setValue(nama.text.toString())
                            .addOnCompleteListener {
                                if (it.isSuccessful)
                                    ref.child(key).child("alamat").setValue(alamat.text.toString())
                                        .addOnCompleteListener {
                                            if (it.isSuccessful)
                                                ref.child(key).child("telepon").setValue(nomorTelepon.text.toString())
                                                    .addOnCompleteListener {
                                                        if (it.isSuccessful)
                                                            ref.child(key).child("fotoCustomer").setValue(urlDownload)
                                                                .addOnCompleteListener {
                                                                    if (it.isSuccessful)
                                                                        ref.child(key).child("lastEdited").setValue(id.toLong())
                                                                            .addOnCompleteListener {
                                                                                if (it.isSuccessful) {
                                                                                    pd?.dismiss()
                                                                                    alert {
                                                                                        message =
                                                                                            "Profile Berhasil DiUpdate"
                                                                                        okButton {
                                                                                            finish()
                                                                                        }
                                                                                    }.show()
                                                                                }
                                                                            }
                                                                }
                                                    }
                                        }
                            }
                    }
                }
            })
    }
}
