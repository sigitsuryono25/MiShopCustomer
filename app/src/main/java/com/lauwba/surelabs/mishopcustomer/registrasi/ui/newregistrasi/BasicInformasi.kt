package com.lauwba.surelabs.mishopcustomer.registrasi.ui.newregistrasi

import agency.tango.materialintroscreen.SlideFragment
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.registrasi.model.Customer
import kotlinx.android.synthetic.main.basic_informasi.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class BasicInformasi : SlideFragment() {

    private var odp: OnDataPass? = null
    private var path: String? = null
    private var gender: String? = null
    private var panggilan: String? = null

    private var message: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.basic_informasi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.titleToolbar?.text = "Informasi Dasar"
        initPanggilan()
        pick.onClick {
            pickerImage()
        }
    }

    private fun pickerImage() {
        val picker = Intent(Intent.ACTION_PICK)
        picker.type = "image/*"
        startActivityForResult(picker, 1024)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1024 && resultCode == Activity.RESULT_OK) {
            path = activity?.let { getRealPathFromUri(it, Uri.parse(data?.dataString)) }
            activity?.let {
                Glide.with(it)
                    .load(path)
                    .apply(RequestOptions().centerCrop().circleCrop().error(R.drawable.ic_profile))
                    .into(previewImage)
            }
        }
    }

    private fun initPanggilan() {
        if (tuan.isChecked) {
            gender = "Laki-Laki"
            panggilan = "Tuan. "
        } else if (nyonya.isChecked) {
            gender = "Perempuan"
            panggilan = "Nyonya. "
        }
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        odp = context as OnDataPass
    }


    override fun canMoveFurther(): Boolean {
        if (nama.text.toString().isEmpty()) {
            message = "Nama Harus Diisi"
            return false
        } else if (ktp.text.toString().isEmpty()) {
            message = "Nomor KTP Harus Diisi"
            return false
        } else if (email.text.toString().isEmpty()) {
            message = "Email Harus Diisi"
            return false
        } else if (telepon.text.toString().isEmpty()) {
            message = "Telepon Harus Diisi"
            return false
        } else if (password.text.toString().isEmpty()) {
            message = "Password Harus Diisi"
            return false
        } else if (confirmPassword.text.toString().isEmpty()) {
            message = "Konfirmasi Password Harus Diisi"
            return false
        } else if (password.text.toString() != confirmPassword.text.toString()) {
            message = "Password tidak cocok"
            return false
        } else {
            val c = Customer()
            c.nama = panggilan + nama.text.toString()
            c.noKTP = ktp.text.toString()
            c.email = email.text.toString()
            c.telepon = telepon.text.toString()
            c.gender = gender

            odp?.onDataPass(c, path, password.text.toString())
            return true
        }
    }

    override fun cantMoveFurtherErrorMessage(): String? {
        return message
    }

    override fun buttonsColor(): Int {
        return R.color.red
    }

    override fun backgroundColor(): Int {
        return R.color.aqua
    }

    interface OnDataPass {
        fun onDataPass(c: Customer?, url: String?, pswd: String)
    }


}
