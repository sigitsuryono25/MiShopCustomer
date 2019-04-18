package com.lauwba.surelabs.mishopcustomer.appintro.newregistrasi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
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

class BasicInformasi : Fragment() {

    private var odp: OnDataPass? = null
    private var path: String? = null

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

    interface OnDataPass {
        fun onDataPass(c: Customer?, url: String?, pswd: String)
    }


}
